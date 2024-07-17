<#macro noauthentication title="Welcome">
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name=viewport content="width=device-width, initial-scale=1">
        <meta name="theme-color" content="#ff835c">

        <title>The Milk Problem</title>
        <link rel="stylesheet" href="/style/reset.css">
        <link rel="stylesheet" href="/style/site.css">
    </head>
    <body>
    <header>
        <div class="container">
            <h1>The Milk Problem</h1>
        </div>
    </header>

    <#nested />

    <footer>
        <div class="container">
            <p>
                &copy;
                <script>document.write(new Date().getFullYear());</script>
                Continuum Collective, Inc. All rights reserved.
                <a href="">Privacy Policy</a>
            </p>
        </div>
    </footer>
    </body>
    </html>
</#macro>