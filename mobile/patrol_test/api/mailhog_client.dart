import 'dart:convert';

import 'package:dio/dio.dart';

import '../config.dart';

/// Reads the mail the backend sent — on the e2e stack, mailhog is the only way out for mail.
///
/// Messages are told apart by id, not by date: snapshot the mailbox with [mailbox] before the
/// request that sends mail, then [waitForNewMail] returns the first id not seen before. No clock is
/// compared, so an older mail can never be picked up by mistake. Same approach as
/// `frontend/e2e/support/mailhog.ts`.
final class MailhogClient {
  final Dio _dio = Dio(BaseOptions(baseUrl: E2eConfig.mailhogUrl));

  Future<Set<String>> mailbox(String to) async =>
      (await _search(to)).map((m) => m['ID'] as String).toSet();

  /// The plain-text content of the first mail to [to] whose id is not in [seen].
  Future<String> waitForNewMail(
    String to,
    Set<String> seen, {
    Duration timeout = const Duration(seconds: 20),
  }) async {
    final deadline = DateTime.now().add(timeout);
    while (DateTime.now().isBefore(deadline)) {
      for (final message in await _search(to)) {
        if (!seen.contains(message['ID'])) return _textOf(message);
      }
      await Future<void>.delayed(const Duration(milliseconds: 250));
    }
    throw StateError('mailhog: no new mail for $to within $timeout');
  }

  /// The `token` query parameter of the first link in [mail].
  String linkTokenIn(String mail) {
    final match = RegExp(r'[?&]token=([^\s&"<>]+)').firstMatch(mail);
    if (match == null) throw StateError('no ?token= link in mail:\n$mail');
    return match.group(1)!;
  }

  Future<List<Map<String, dynamic>>> _search(String to) async {
    final response = await _dio.get<Map<String, dynamic>>(
      '/api/v2/search',
      queryParameters: {'kind': 'to', 'query': to, 'limit': 250},
    );
    return ((response.data?['items'] as List?) ?? const [])
        .cast<Map<String, dynamic>>();
  }

  String _textOf(Map<String, dynamic> message) {
    final leaves = <Map<String, dynamic>>[];
    void walk(Map<String, dynamic> part) {
      final parts = (part['MIME'] as Map?)?['Parts'] as List?;
      if (parts != null && parts.isNotEmpty) {
        parts.cast<Map<String, dynamic>>().forEach(walk);
      } else {
        leaves.add(part);
      }
    }

    final parts = (message['MIME'] as Map?)?['Parts'] as List?;
    if (parts != null && parts.isNotEmpty) {
      parts.cast<Map<String, dynamic>>().forEach(walk);
    } else {
      leaves.add(message['Content'] as Map<String, dynamic>);
    }
    final plain = leaves
        .where((p) => _header(p, 'Content-Type').startsWith('text/plain'))
        .toList();
    return (plain.isEmpty ? leaves : plain).map(_decode).join('\n');
  }

  String _header(Map<String, dynamic> part, String name) {
    final headers = part['Headers'] as Map<String, dynamic>;
    for (final entry in headers.entries) {
      if (entry.key.toLowerCase() == name.toLowerCase()) {
        final values = entry.value as List;
        return values.isEmpty ? '' : values.first as String;
      }
    }
    return '';
  }

  String _decode(Map<String, dynamic> part) {
    final body = part['Body'] as String;
    switch (_header(part, 'Content-Transfer-Encoding').toLowerCase()) {
      case 'base64':
        return utf8.decode(base64.decode(body.replaceAll(RegExp(r'\s+'), '')));
      case 'quoted-printable':
        final joined = body.replaceAll(RegExp(r'=\r?\n'), '');
        final bytes = <int>[];
        for (var i = 0; i < joined.length; i++) {
          final hex = i + 2 < joined.length ? joined.substring(i + 1, i + 3) : '';
          if (joined[i] == '=' && RegExp(r'^[0-9A-Fa-f]{2}$').hasMatch(hex)) {
            bytes.add(int.parse(hex, radix: 16));
            i += 2;
          } else {
            bytes.addAll(utf8.encode(joined[i]));
          }
        }
        return utf8.decode(bytes, allowMalformed: true);
      default:
        return body;
    }
  }
}
