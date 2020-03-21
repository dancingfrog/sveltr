import './styles/global.css';
import App from './apps/App.svelte';
import TestApp from './apps/TestApp.svelte';
// GuideApp code is from https://github.com/PacktPublishing/Svelte.js---The-Complete-Guide.git
import GuideApp from './guide/base-syntax-04-nested-components/src/App.svelte';

const appId = "svelte-app";
const appElement = document.getElementById(appId);
export default ( // Check if app id exists in DOM
    appElement !== null &&
    (appElement.constructor.name === 'HTMLElement' ||
        appElement.constructor.name === 'HTMLDivElement')
    ) ?
    new App({
        target: appElement,
        props: {
            greeting:
`Hooray 🎉 - you've built this with <a href="https://github.com/dancingfrog/sveltr" target="_blank">Sveltr</a>!`
        }
    }) : {};

const testAppId = "test-app";
const testAppElement = document.getElementById(testAppId);
export const testApp = (
    testAppElement !== null &&
    (testAppElement.constructor.name === 'HTMLElement' ||
        testAppElement.constructor.name === 'HTMLDivElement')
    ) ?
    new TestApp({
        target: testAppElement,
        props: {
            title: "🦊 Hello Svelte!"
        }
    }) : {};

// Examples from the course:
// "Svelte.js - The Complete Guide"
// by Maximilian Schwarzmüller (Acade Mind)
// https://www.packtpub.com/web-development/svelte-js-the-complete-guide-video
// https://github.com/PacktPublishing/Svelte.js---The-Complete-Guide.git
const guideAppId = "svelte-js-the-complete-guide";
const guideAppElement = document.getElementById(guideAppId);
export const guideApp = ( // Check if app id exists in DOM
    guideAppElement !== null &&
    (guideAppElement.constructor.name === 'HTMLElement' ||
        guideAppElement.constructor.name === 'HTMLDivElement')
) ?
    new GuideApp({
        target: guideAppElement,
        props: {
            name:
                `Dev`
        }
    }) : {};
