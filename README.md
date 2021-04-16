# Victorious Hartford

Candidates for Selectboard 2021

|                             |                             |
|-----------------------------|-----------------------------|
| Dan Fraser      &nbsp;&nbsp;|                 &nbsp;&nbsp;|
| John Hall       &nbsp;&nbsp;| Rachel Edens    &nbsp;&nbsp;|
| Ally Tufenkjian &nbsp;&nbsp;| Julia Dalphin   &nbsp;&nbsp;|

<em>Victorious Hartford&nbsp;</em> or <em>Victory For Hartford&nbsp;</em> means discovering and building strength from within our community. By strength, I mean creative problem solving, health, resilience, trust and well-being. We can see that the challenges of the recent past and of the near future will continue to push our current resources and systems to the absolute limit of their capacity to respond and to stabilize. We, and by we I mean the entire community, must consciously and continuously re-evaluate and re-invest in our collective capabilities ── enhancing our strengths, shining light on our blind spots and working through our shortcomings ── in order to overcome these challenges.

Hartford is a land of three rivers cradling countless flora and fauna. Trees cover a cumulative 23 square miles of the total 45 square mile expanse that is Hartford. Across that expanse, almost ten thousand souls live and labor and depend on the stability of the land beneath their feet and the social and economic systems that bind our realities together. These systems are as fragile as any forest ecosystem, possibly more so, and like any river, great care and maintenance and awareness are required to keep the water flowing to every place and every mouth that needs it.

As issues within our community come to the surface, be they challenges to our economic well-being, environmental health, social cohesion, or otherwise, we must honestly evaluate strategies to deepen our understanding of these challenges and plot a course of action that will fundamentally improve the ways that the Town relates to its personnel and to its residents. I understand that both the process of acknowledging real issues, and the genesis of effective strategies must be driven by the groups on which those issues have the greatest impact. We need to frequently remind ourselves of that dynamic value and accept that when we allow the powers of choice and action to stagnate, we lose the ability to adapt to disasters of all forms, be they biological, climactic, or economic. Every person in our community must be granted access and agency over these powers of governance in order for all of us to be victorious.

## Usage

### :exclamation: Prerequisites

SveltR requires a working R with the blogdown package, the latest/LTS [node](https://nodejs.org/en/download/) and [npm](https://www.npmjs.com/get-npm). You will need to have installed the following native libraries on the host operating system in order for R to compile some of the examples (see [tech.Rmd](content/tech.Rmd)):
pandoc gdal geos proj udunits2

Next step, clone this repository and run:

```bash
npm install
```

This will take some time and will install all packages necessary to run Sveltr and its tasks.

### :construction_worker: Development

While developing your website use:

```bash
npm start:dev
```

Then visit http://localhost:4321 _- or whatever local host and port number that blogdown/servr displays in the terminal -_ to preview your new website. Svelte Dev Server will automatically reload the CSS and Javascript when the bundled stylesheets and scripts in the  src folder change, while blogdown will rebuild the static pages when the content changes.

### :package: Static build

To build a static version of the website inside the `/public` folder, run:

```bash
npm run build
```

To get a preview of posts or articles not yet published, run:

```bash
npm run build:preview
```

See [package.json](package.json#L8) for all tasks.

## Structure

```
|--content          // Pages and collections - ask if you need extra pages
|--data             // YAML data files with any data for use in examples
|--layouts          // This is where all templates go
|  |--partials      // This is where includes live
|  |--index.html    // The index page
|--static           // Files in here ends up in the public folder
|--src                 // Files that will pass through the asset pipeline
|  |--App.svelte              // Add Svelte apps with the extension .svelte
|  |--main.js         // main.js is the Svelte/Webpack entry for your reactive assets
|--themes           // Install Hugo theme here and reference in config.toml
```

## Basic Concepts

You can read more about Hugo's template language in their documentation here:

https://gohugo.io/templates/overview/

The most useful page there is the one about the available functions:

https://gohugo.io/templates/functions/

For assets that are completely static and don't need to go through the asset pipeline,
use the `static` folder. Images, font-files, etc, all go there.

Files in the static folder end up in the web root. So a file called `static/favicon.ico`
will end up being available as `/favicon.ico` and so on...

The `src/main.js` file is the entrypoint for Svelte and will be built to `/public/main.js`

You can use **ES6** and use both relative imports or import libraries from npm.

## Environment variables

To separate the development and production _- aka build -_ stages, all gulp tasks run with a node environment variable named either `development` or `production`.

You can access the environment variable inside the theme files with `getenv "NODE_ENV"`. See the following example for a conditional statement:

    {{ if eq (getenv "NODE_ENV") "development" }}You're in development!{{ end }}

All tasks starting with _build_ set the environment variable to `production` - the other will set it to `development`.

## Enjoy!! 😸
