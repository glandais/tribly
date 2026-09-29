vcl 4.1;

backend default {
    .host = "imgproxy";
    .port = "8080";
}

sub vcl_recv {
    # Storage re-encoding an upload to strip its metadata (backend ImgProxyClient.reencode,
    # docs/LEDGER_*.md API-43): each URL names a temporary object and is fetched once, so caching
    # it would only push real thumbnails out.
    if (req.url ~ "^/insecure/sm:1/") {
        return (pass);
    }
}
