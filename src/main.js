import './styles/global.css';
import App from './apps/App.svelte';
import GLApp from './apps/GLApp.svelte';
import TestApp from './apps/TestApp.svelte';
import VizRApp from './apps/VizRApp.svelte';


const glAppId = "gl-app";
const glAppElement = document.getElementById(glAppId);
export const glApp = (
    glAppElement !== null &&
    (glAppElement.constructor.name === 'HTMLElement' ||
        glAppElement.constructor.name === 'HTMLDivElement')
) ?
    new GLApp({
        target: glAppElement,
        props: {
            title: "🦊 Hello SvelteGL!"
        }
    }) : {};

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

// A special reference for running the VizR app on www.real-currents.com
const vizrAppId = "uni-sol";
const vizrAppElement = document.getElementById(vizrAppId);
if (
    vizrAppElement !== null &&
    (vizrAppElement.constructor.name === 'HTMLElement' ||
        vizrAppElement.constructor.name === 'HTMLDivElement')
)  {
    const numChildren = vizrAppElement.children.length;
    for (let c=(numChildren - 1); c >= 0; c--)
        vizrAppElement.removeChild(vizrAppElement.children[c]);
}
export const vizrApp = (
    vizrAppElement !== null &&
    (vizrAppElement.constructor.name === 'HTMLElement' ||
        vizrAppElement.constructor.name === 'HTMLDivElement')
    ) ?
    new VizRApp({
        target: vizrAppElement,
        props: {
            title: "🦊 Hello SvelteGL!"
        }
    }) : {};
