/* The following builtins are prepended to
 * every custom vertex shader in @svelte/gl:
 */
/* start builtins */
//
//precision highp float;
//
//uniform mat4 MODEL;
//uniform mat4 PROJECTION;
//uniform mat4 VIEW;
//uniform mat4 MODEL_INVERSE_TRANSPOSE;
//
//uniform vec3 CAMERA_WORLD_POSITION;
//
//struct PointLight {
//	vec3 location;
//	vec3 color;
//	float intensity;
//// TODO fall-off etc
//};
//
//uniform PointLight POINT_LIGHTS[NUM_LIGHTS];
/* end builtins */

#define NAME terrain-vert

#define DISPLACE_MULTIPLY 0.99

// texture containing elevation data
//uniform sampler2D heightMap;
uniform sampler2D bumpmap;

in vec3 position;

in vec3 normal;

in vec2 uv; // available when texture maps (bumpmap, colormap, ...) are used on object

out vec3 v_normal;

out vec2 v_textureCoords;

void main() {
	float displacement = texture(bumpmap, uv).r;

	vec3 displace_along_normal = vec3(normal * displacement);

	vec3 displaced_position = position + (DISPLACE_MULTIPLY * displace_along_normal);

	// NEED TO CALCULATE NORMAL DISPLACEMENT BY SAMPLING NEIGHBOR UV's
	v_normal = normal + displacement;

	v_textureCoords = uv;

	gl_Position = PROJECTION * VIEW * MODEL * vec4(displaced_position, 1.0);
}
