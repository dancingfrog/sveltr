import './styles/global.css';
import App from './apps/App.svelte';
import GLApp from './apps/GLApp.svelte';
import GLSLApp from './apps/GLSLApp.svelte';
import TechApp from './apps/TechApp.svelte';
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

const glslAppId = "glsl-app";
const glslAppElement = document.getElementById(glslAppId);
export const glslApp = (
    glslAppElement !== null &&
    (glslAppElement.constructor.name === 'HTMLElement' ||
        glslAppElement.constructor.name === 'HTMLDivElement')
) ?
    new GLSLApp({
        target: glslAppElement,
        props: {
            title: "🦊 Hello SvelteGL!"
        }
    }) : {};

const appId = 'svelte-app';
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
`Hooray 🎉 - you've built this with <a href='https://github.com/dancingfrog/sveltr' target='_blank'>Sveltr</a>!`
        }
    }) : {};


const techAppId = 'tech-app';
const techAppElement = document.getElementById(techAppId);
export const techApp = (
    techAppElement !== null &&
    (techAppElement.constructor.name === 'HTMLElement' ||
        techAppElement.constructor.name === 'HTMLDivElement')
    ) ?
    new TechApp({
        target: techAppElement,
        props: {
            title: '🦊 Hello Svelte!'
        }
    }) : {};


const glAppId = 'gl-app';
const glAppElement = document.getElementById(glAppId);
export const glApp = (
    glAppElement !== null &&
    (glAppElement.constructor.name === 'HTMLElement' ||
        glAppElement.constructor.name === 'HTMLDivElement')
    ) ?
    new GLApp({
        target: glAppElement,
        props: {
            title: '🦊 Hello SvelteGL!'
        }
    }) : {};


const vizrAppId = 'uni-sol';
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
            title: 'Visualizing R Data with Sveltr'
        }
    }) : {};
