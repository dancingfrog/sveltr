/* This builtin is prepended to every
 * custom vertext shader in Svelte:
 */
/* start builtins */
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

attribute vec3 position;
attribute vec3 normal;

varying vec3 v_normal;

void main() {
	vec4 pos = vec4(position, 1.0);
	vec4 model_view_pos = VIEW * MODEL * pos;

	v_normal = (MODEL_INVERSE_TRANSPOSE * vec4(normal, 0.0)).xyz;

	gl_Position = PROJECTION * model_view_pos;
}
