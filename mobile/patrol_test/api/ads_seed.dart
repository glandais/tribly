import 'package:dio/dio.dart';

import 'backend_client.dart';
import 'mailpit_client.dart';

/// Seeding of the classified ads and of their contact relay.
extension AdsSeed on BackendClient {
  /// A published sale ad with a place: [locationDescription], and the point [lonLat] the API blurs
  /// to the centre of its ~1 km cell before any member reads it.
  Future<Json> newAdWithLocation(
    TestUser by,
    String teamSlug,
    String label, {
    required String locationDescription,
    required List<double> lonLat,
  }) => post(by, '/api/teams/$teamSlug/classifieds', {
    'name': unique(label),
    'status': 'PUBLISHED',
    'adType': 'SALE',
    'price': 80,
    'media': markdownMedia(),
    'locationDescription': locationDescription,
    'locationGeometry': {'type': 'Point', 'coordinates': lonLat},
  });

  /// The profile switch « Recevoir les messages des membres au sujet de mes annonces ».
  Future<void> setContactable(TestUser who, bool contactable) => patch(
    who,
    '/api/users/me/preferences',
    {'contactableByMembers': contactable},
  );

  /// One message through the relay, straight to the API — to spend the sender's quota (10 an hour).
  Future<void> contactSeller(
    TestUser sender,
    String teamSlug,
    String adSlug,
    String message,
  ) => post(sender, '/api/teams/$teamSlug/classifieds/$adSlug/contact', {
    'message': message,
  });

  /// One message to the seller of an ad through the relay: the error `code` the server answers,
  /// or null when it took the message (204).
  Future<String?> contactSellerError(
    TestUser sender,
    String teamSlug,
    String adSlug,
  ) async {
    final response = await http.post<Json>(
      '/api/teams/$teamSlug/classifieds/$adSlug/contact',
      data: {'message': 'Bonjour, votre annonce est-elle toujours en ligne ?'},
      options: Options(
        headers: {'Authorization': 'Bearer ${sender.accessToken}'},
        validateStatus: (_) => true,
      ),
    );
    return response.statusCode == 204
        ? null
        : response.data?['code'] as String?;
  }
}

/// The mails the contact relay sent.
extension AdContactMail on MailpitClient {
  /// The relayed mails about the ad named [adName] in [sellerEmail]'s mailbox — the sign-up mail
  /// does not name it. Whole messages, envelope included (`From`, `ReplyTo`, `Text`, `HTML`).
  Future<List<Json>> relayedAbout(String sellerEmail, String adName) async => [
    for (final message in await messagesTo(sellerEmail))
      if ('${message['Subject']}\n${message['Text']}\n${message['HTML']}'
          .contains(adName))
        message,
  ];
}

/// The addresses of a mailpit address list (`From` is one entry, `ReplyTo` a list).
List<String> addressesIn(Object? field) => [
  for (final entry in field is List ? field : [field])
    if (entry is Map) entry['Address'] as String,
];
