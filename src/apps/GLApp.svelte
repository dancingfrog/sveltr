    <script>
        import { onMount } from 'svelte';
        import * as GL from '@sveltejs/gl';
        import terrainVert from './shaders/custom/terrain-vert.glsl';

        export let title;

        export let color = '#ff3e00';

        let w = 1;
        let h = 1;
        let d = 1;

        const light = {};

        function adjustColor (clr, height = 1) {
            const r = parseInt('0x' + clr.substr(1, 2), 16),
              g = parseInt('0x' + clr.substr(3, 2), 16),
              b = parseInt('0x' + clr.substr(5, 2), 16);

            const hr = Math.floor(r * (height / 0.25)),
              hb = Math.floor(b * (height / 0.25));
            return Math.abs((((hr < 255) ? hr : r) << 16) + (g << 8) + ((hb < 255) ? hb : b));
        }

        let terrain;
        const terrainMap = new Image();
        const heightMap = new Image();
        terrainMap.alt = 'Terrain Texture';
        heightMap.crossOrigin = terrainMap.crossOrigin = '';

        let webgl;
        let displacementTexture = null;
        let terrainTexture = null;
        let process_extra_shader_components = (gl, material, model) => {
            // console.log("Process Extra Shader Components");
            const program = material.program;

            if (material.vertName === "terrain-vert") {
                // console.log(material.vertName);

                if (!!terrainTexture && !!displacementTexture) {
                    const displacementTextureLocation = gl.getUniformLocation(program, "heightMap");

                    gl.activeTexture(gl.TEXTURE1);
                    gl.bindTexture(gl.TEXTURE_2D, displacementTexture);
                    gl.uniform1i(displacementTextureLocation, 1);


                }

            }

        };

        onMount(() => {
            let frame;

            console.log(webgl);

            if (!!displacementTexture === false) {
                // Create a texture and create initial bind
                displacementTexture = webgl.createTexture();
                webgl.bindTexture(webgl.TEXTURE_2D, displacementTexture);
                webgl.bindTexture(webgl.TEXTURE_2D, null);
            }

            // Texture constants
            const level = 0;
            const internalFormat = webgl.RGBA;
            const format = webgl.RGBA;
            const type = webgl.UNSIGNED_BYTE;

            heightMap.addEventListener('load', function () {
                // Now that the image has loaded copy it to the texture.
                console.log("Bind to texture");

                webgl.bindTexture(webgl.TEXTURE_2D, displacementTexture);
                webgl.texImage2D(webgl.TEXTURE_2D, level, internalFormat, format, type, heightMap);
                webgl.generateMipmap(webgl.TEXTURE_2D);
                webgl.texParameteri(webgl.TEXTURE_2D, webgl.TEXTURE_MAG_FILTER, webgl.NEAREST_MIPMAP_LINEAR);
                webgl.texParameteri(webgl.TEXTURE_2D, webgl.TEXTURE_MIN_FILTER, webgl.NEAREST_MIPMAP_LINEAR);
            });

            heightMap.src = "images/heightmap.png";

            terrain = new GL.Texture("/images/heightmap.png", { width: 512, height: 512 });

            const loop = () => {
                frame = requestAnimationFrame(loop);

                light.x = 3 * Math.sin(Date.now() * 0.001);
                light.y = 2.5 + 2 * Math.sin(Date.now() * 0.0004);
                light.z = 3 * Math.cos(Date.now() * 0.002);
            };

            loop();

            return () => cancelAnimationFrame(frame);
        });
    </script>

    <GL.Scene bind:gl={webgl} backgroundOpacity=1.0 process_extra_shader_components={process_extra_shader_components}>
        <GL.Target id="center" location={[0, h/2, 0]}/>

        <GL.OrbitControls maxPolarAngle={Math.PI / 2} let:location>
            <GL.PerspectiveCamera {location} lookAt="center" near={0.01} far={1000}/>
        </GL.OrbitControls>

        <GL.AmbientLight intensity={0.3}/>
        <GL.DirectionalLight direction={[-1,-1,-1]} intensity={0.5}/>

        <!-- ground -->
        <GL.Mesh
          geometry={GL.terrain()}
          location={[0, -0.01, 0]}
          rotation={[-90, 0, 0]}
          scale={h}
          vert={terrainVert}
          uniforms={{ color: 0xffffff, colormap: terrain }}
        />

        <!-- moving light -->
        <GL.Group location={[light.x,light.y,light.z]}>
            <GL.Mesh
                    geometry={GL.sphere({ turns: 36, bands: 36 })}
                    location={[0,0.2,0]}
                    scale={0.1}
                    uniforms={{ color: adjustColor(color, h), emissive: adjustColor(color) }}
            />

            <GL.PointLight
                    location={[0,0,0]}
                    color={adjustColor(color, h)}
                    intensity={0.6}
            />
        </GL.Group>
    </GL.Scene>

    <div class="controls">
        <label>
            <input type="color" style="height: 64px" bind:value={color}>
        </label>

        <label>
            <input type="range" bind:value={h} min={0.5} max={2} step={0.1}><br />
            size ({h})
        </label>
    </div>
