import 'package:dio/dio.dart' show Dio;
import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:path_provider/path_provider.dart';
import 'package:share_plus/share_plus.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../data/route_repository.dart';
import '../../../../core/widgets/widgets.dart';
import '../widgets/route_map.dart';

final routeDetailProvider = FutureProvider.family<RouteDetailDto,
    ({String teamSlug, String routeSlug})>((ref, params) async {
  final repository = ref.watch(routeRepositoryProvider);
  return repository.getRoute(params.teamSlug, params.routeSlug);
});

class RouteDetailPage extends ConsumerWidget {
  final String teamSlug;
  final String routeSlug;

  const RouteDetailPage({
    super.key,
    required this.teamSlug,
    required this.routeSlug,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final params = (teamSlug: teamSlug, routeSlug: routeSlug);
    final routeAsync = ref.watch(routeDetailProvider(params));

    return routeAsync.when(
      data: (route) => _RouteDetailContent(route: route),
      loading: () => Scaffold(
        appBar: AppBar(),
        body: const Center(child: CircularProgressIndicator()),
      ),
      error: (error, stack) => Scaffold(
        appBar: AppBar(),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.error_outline, size: 48, color: Theme.of(context).colorScheme.error),
              const SizedBox(height: 16),
              Text(getErrorMessage(error)),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () => ref.invalidate(routeDetailProvider(params)),
                child: Text('common.retry'.tr()),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _RouteDetailContent extends ConsumerWidget {
  final RouteDetailDto route;

  const _RouteDetailContent({required this.route});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      body: Stack(
        children: [
          // Full-screen map
          RouteMap(route: route),

          // Top bar overlay
          Positioned(
            top: 0,
            left: 0,
            right: 0,
            child: SafeArea(
              bottom: false,
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                child: Row(
                  children: [
                    Material(
                      color: Theme.of(context)
                          .colorScheme
                          .surface
                          .withValues(alpha: 0.8),
                      shape: const CircleBorder(),
                      child: IconButton(
                        icon: const Icon(Icons.arrow_back),
                        onPressed: () => Navigator.of(context).pop(),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Material(
                        color: Theme.of(context)
                            .colorScheme
                            .surface
                            .withValues(alpha: 0.8),
                        borderRadius: BorderRadius.circular(20),
                        child: Padding(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 16, vertical: 8),
                          child: Text(
                            route.name,
                            style: Theme.of(context).textTheme.titleMedium,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),

          // Bottom sheet
          DraggableScrollableSheet(
            initialChildSize: 0.15,
            minChildSize: 0.1,
            maxChildSize: 0.7,
            snap: true,
            snapSizes: const [0.15, 0.45],
            builder: (context, scrollController) {
              return Container(
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surface,
                  borderRadius:
                      const BorderRadius.vertical(top: Radius.circular(16)),
                  boxShadow: [
                    BoxShadow(
                      color: Theme.of(context).shadowColor.withValues(alpha: 0.2),
                      blurRadius: 10,
                      offset: const Offset(0, -2),
                    ),
                  ],
                ),
                child: SingleChildScrollView(
                  controller: scrollController,
                  child: Column(
                    children: [
                      // Drag handle
                      Padding(
                        padding: const EdgeInsets.symmetric(vertical: 8),
                        child: Container(
                          width: 40,
                          height: 4,
                          decoration: BoxDecoration(
                            color: Theme.of(context)
                                .colorScheme
                                .onSurfaceVariant
                                .withValues(alpha: 0.4),
                            borderRadius: BorderRadius.circular(2),
                          ),
                        ),
                      ),

                      // Stats row
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 16),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceAround,
                          children: [
                            _StatItem(
                              icon: Icons.straighten,
                              value:
                                  '${(route.distance / 1000).toStringAsFixed(1)} km',
                              label: 'routes.distance'.tr(),
                            ),
                            _StatItem(
                              icon: Icons.trending_up,
                              value: '${route.elevationGain.toInt()} m',
                              label: 'routes.elevation'.tr(),
                            ),
                            _StatItem(
                              icon: Icons.trending_down,
                              value: '${route.elevationLoss.toInt()} m',
                              label: 'routes.elevationDown'.tr(),
                            ),
                          ],
                        ),
                      ),

                      const SizedBox(height: 16),

                      // Surface type
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 16),
                        child: Card(
                          child: ListTile(
                            leading: Icon(
                              AppFormatters.surfaceIcon(route.surfaceType),
                              color: Theme.of(context).colorScheme.primary,
                            ),
                            title: Text('routes.surface'.tr()),
                            subtitle: Text(
                                AppFormatters.surfaceName(route.surfaceType)),
                          ),
                        ),
                      ),

                      // Description
                      if (route.media.markdown.isNotEmpty)
                        Padding(
                          padding: const EdgeInsets.all(16),
                          child: Card(
                            child: Padding(
                              padding: const EdgeInsets.all(16),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    'routes.description'.tr(),
                                    style:
                                        Theme.of(context).textTheme.titleMedium,
                                  ),
                                  const SizedBox(height: 8),
                                  MarkdownContent(
                                    data: route.media.markdown,
                                    images: route.media.assets.images,
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ),

                      // Download button
                      _DownloadButton(route: route),

                      const SizedBox(height: 16),
                    ],
                  ),
                ),
              );
            },
          ),
        ],
      ),
    );
  }

}

class _DownloadButton extends ConsumerWidget {
  final RouteDetailDto route;

  const _DownloadButton({required this.route});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final hasDownloads =
        route.media.assets.gpx != null || route.media.assets.fit != null;
    final connectedServices =
        ref.watch(authProvider).user?.connectedServices ?? [];
    final hasActions = hasDownloads || connectedServices.isNotEmpty;

    if (!hasActions) return const SizedBox.shrink();

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: SizedBox(
        width: double.infinity,
        child: FilledButton.icon(
          onPressed: () => _showDownloadSheet(context, ref),
          icon: const Icon(Icons.download),
          label: Text('routes.download'.tr()),
        ),
      ),
    );
  }

  void _showDownloadSheet(BuildContext context, WidgetRef ref) {
    final connectedServices =
        ref.read(authProvider).user?.connectedServices ?? [];

    showModalBottomSheet(
      context: context,
      builder: (sheetContext) => SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (route.media.assets.gpx != null)
                _FileDownloadTile(
                  asset: route.media.assets.gpx!,
                  label: 'routes.downloadGpx'.tr(),
                  dio: ref.read(dioProvider),
                ),
              if (route.media.assets.fit != null)
                _FileDownloadTile(
                  asset: route.media.assets.fit!,
                  label: 'routes.downloadFit'.tr(),
                  dio: ref.read(dioProvider),
                ),
              if (connectedServices.isNotEmpty) ...[
                const Divider(),
                Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                  child: Align(
                    alignment: Alignment.centerLeft,
                    child: Text(
                      'routes.sendToDevice'.tr(),
                      style: Theme.of(sheetContext).textTheme.titleSmall,
                    ),
                  ),
                ),
                ...connectedServices.map(
                  (service) => _DeviceUploadTile(
                    service: service,
                    teamSlug: route.team.slug,
                    routeSlug: route.slug,
                  ),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}

class _FileDownloadTile extends StatefulWidget {
  final AssetDto asset;
  final String label;
  final Dio dio;

  const _FileDownloadTile({
    required this.asset,
    required this.label,
    required this.dio,
  });

  @override
  State<_FileDownloadTile> createState() => _FileDownloadTileState();
}

class _FileDownloadTileState extends State<_FileDownloadTile> {
  bool _isDownloading = false;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: _isDownloading
          ? const SizedBox(
              width: 24,
              height: 24,
              child: CircularProgressIndicator(strokeWidth: 2),
            )
          : const Icon(Icons.download),
      title: Text(widget.label),
      onTap: _isDownloading ? null : _download,
    );
  }

  Future<void> _download() async {
    setState(() => _isDownloading = true);
    try {
      final dir = await getTemporaryDirectory();
      final filePath = '${dir.path}/${widget.asset.fileName}';
      await widget.dio.download(widget.asset.url, filePath);
      if (mounted) {
        final box = context.findRenderObject() as RenderBox?;
        final origin = box != null
            ? box.localToGlobal(Offset.zero) & box.size
            : Rect.zero;
        Navigator.pop(context);
        await SharePlus.instance.share(
          ShareParams(
            files: [XFile(filePath)],
            sharePositionOrigin: origin,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        Navigator.pop(context);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              getErrorMessage(e),
            ),
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isDownloading = false);
      }
    }
  }
}

class _DeviceUploadTile extends ConsumerStatefulWidget {
  final GpsServiceConnectionDto service;
  final String teamSlug;
  final String routeSlug;

  const _DeviceUploadTile({
    required this.service,
    required this.teamSlug,
    required this.routeSlug,
  });

  @override
  ConsumerState<_DeviceUploadTile> createState() => _DeviceUploadTileState();
}

class _DeviceUploadTileState extends ConsumerState<_DeviceUploadTile> {
  bool _isUploading = false;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: _isUploading
          ? const SizedBox(
              width: 24,
              height: 24,
              child: CircularProgressIndicator(strokeWidth: 2),
            )
          : const Icon(Icons.smartphone),
      title: Text(widget.service.displayName),
      onTap: _isUploading ? null : _upload,
    );
  }

  Future<void> _upload() async {
    setState(() => _isUploading = true);
    try {
      final client = ref.read(gpsServicesClientProvider);
      await client.uploadRoute(
        serviceType: GpsServiceType.fromJson(widget.service.serviceType),
        teamSlug: widget.teamSlug,
        routeSlug: widget.routeSlug,
      );
      if (mounted) {
        Navigator.pop(context);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('routes.uploadSuccess'.tr())),
        );
      }
    } catch (e) {
      if (mounted) {
        Navigator.pop(context);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              getErrorMessage(e),
            ),
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isUploading = false);
      }
    }
  }
}

class _StatItem extends StatelessWidget {
  final IconData icon;
  final String value;
  final String label;

  const _StatItem({
    required this.icon,
    required this.value,
    required this.label,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Icon(icon, color: Theme.of(context).colorScheme.primary),
        const SizedBox(height: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
        ),
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall,
        ),
      ],
    );
  }
}
