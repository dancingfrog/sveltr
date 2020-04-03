/* The following builtins are prepended to
 * every custom vertex shader in @svelte/gl:
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

#define NAME normal-selected-txt-vertex-shader

in vec3 position;
in vec3 normal;

in vec2 vertexTextureCoords;

out vec2 v_textureCoords;

out vec3 v_view_position;

out vec3 v_normal;

out vec3 v_objNormal;

void main() {
	vec4 pos = vec4(position, 1.0);
	vec4 model_view_pos = VIEW * MODEL * pos;

	v_normal = (MODEL_INVERSE_TRANSPOSE * vec4(normal, 0.0)).xyz;

	v_objNormal = normal;

	v_view_position = model_view_pos.xyz;

	v_textureCoords = vertexTextureCoords;

	gl_Position = PROJECTION * model_view_pos;
}
