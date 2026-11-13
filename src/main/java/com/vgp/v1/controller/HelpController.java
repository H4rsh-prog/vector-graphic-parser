package com.vgp.v1.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelpController {
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getApiManual() {
        return """
            <!doctype html>
            <html lang="en">
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Image to SVG API Help</title>
                <style>
                    body { color: #202820; font: 16px/1.6 system-ui, sans-serif; margin: 0; }
                    main { margin: 0 auto; max-width: 860px; padding: 32px 20px; }
                    h1, h2 { line-height: 1.25; }
                    code { background: #f0f3ef; border-radius: 3px; padding: 2px 5px; }
                    table { border-collapse: collapse; width: 100%; }
                    th, td { border-bottom: 1px solid #d9dfd8; padding: 10px; text-align: left; vertical-align: top; }
                    th { background: #f4f6f3; }
                    .note { border-left: 4px solid #b46b35; padding-left: 12px; }
                    @media (max-width: 600px) { table { font-size: 14px; } th, td { padding: 8px 5px; } }
                </style>
            </head>
            <body>
                <main>
                    <h1>Turn an image into an SVG</h1>
                    <p>Send an image to the vector conversion service and receive an SVG drawing in return. An SVG can be resized without becoming pixelated.</p>

                    <h2>Where to send your request</h2>
                    <p><strong>POST</strong> <code>/v1/vectors</code></p>
                    <p>Send the request as a form upload (<code>multipart/form-data</code>). Choose an image file and provide the settings below.</p>

                    <h2>What to include</h2>
                    <table>
                        <thead>
                            <tr><th>Form field</th><th>Needed?</th><th>What it means</th></tr>
                        </thead>
                        <tbody>
                            <tr><td><code>image</code></td><td>Yes</td><td>The image file you want to convert.</td></tr>
                            <tr><td><code>blur_strength</code></td><td>Yes</td><td>How much to soften the image before tracing its shapes.</td></tr>
                            <tr><td><code>binary_mask_weight</code></td><td>No</td><td>Adjusts how the service distinguishes the image's shapes from its background. If omitted, the value is 0.</td></tr>
                            <tr><td><code>simplfication_threshold</code></td><td>Yes</td><td>Controls how much small detail is removed from the traced outline. The field name is spelled exactly this way in the current API.</td></tr>
                            <tr><td><code>curve_tolerance</code></td><td>Yes</td><td>Controls how closely the smooth curves follow the traced outline.</td></tr>
                        </tbody>
                    </table>

                    <h2>What you receive</h2>
                    <p>A successful request returns the SVG drawing as text. Save the response with an <code>.svg</code> file extension to open it in a browser or graphics program.</p>
                    <p class="note">All settings except <code>image</code> are numbers. The service currently documents no fixed recommended ranges for these settings.</p>
                </main>
            </body>
            </html>
            """;
    }
}