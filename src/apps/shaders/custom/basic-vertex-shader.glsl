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

#if defined(has_colormap) || defined(has_specularitymap) || defined(has_normalmap) || defined(has_bumpmap)
#define has_textures true
#endif

#ifdef has_textures
in vec2 uv;
out vec2 v_uv;
#endif

#if defined(has_normalmap) || defined(has_bumpmap)
out vec3 v_view_position;
#endif

out vec3 v_surface_to_light[NUM_LIGHTS];
out vec3 v_surface_to_view[NUM_LIGHTS];

#ifdef USE_FOG
out float v_fog_depth;
#endif

in vec3 position;
in vec3 normal;

out vec3 v_normal;

void main() {
	vec4 pos = vec4(position, 1.0);
	vec4 model_view_pos = VIEW * MODEL * pos;

	v_uv = uv;

	v_normal = (MODEL_INVERSE_TRANSPOSE * vec4(normal, 0.0)).xyz;

	gl_Position = PROJECTION * model_view_pos;
}
