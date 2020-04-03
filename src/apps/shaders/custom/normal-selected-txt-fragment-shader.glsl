/* The following builtins are prepended to
 * every custom fragment shader in @svelte/gl:
 */
/* start builtins */
//#extension GL_OES_standard_derivatives : enable

//precision highp float;
//
//struct DirectionalLight {
//	vec3 direction;
//	vec3 color;
//	float intensity;
//};
//
//struct PointLight {
//	vec3 location;
//	vec3 color;
//	float intensity;
//// TODO fall-off etc
//};
//
//uniform vec3 AMBIENT_LIGHT;
//uniform DirectionalLight DIRECTIONAL_LIGHTS[NUM_LIGHTS];
//uniform PointLight POINT_LIGHTS[NUM_LIGHTS];
/* end builtins */

#define NAME normal-selected-txt-fragment-shader

uniform vec3 color;

#ifdef has_alpha
uniform float alpha;
#endif

uniform sampler2D uTexture0;
uniform sampler2D uTexture1;
uniform sampler2D uTexture2;
uniform sampler2D uTexture3;
uniform sampler2D uTexture4;
uniform sampler2D uTexture5;

in vec3 v_normal;

in vec3 v_objNormal;

in vec2 v_textureCoords;

out mediump vec4 fragColor;

void main () {
	vec3 normal = normalize(v_normal);

	if (v_objNormal.z == 1.0) {
		fragColor = texture(uTexture0, v_textureCoords);

	} else if (v_objNormal.x == -1.0) {
//		fragColor = vec4(1.0, 0.0, 1.0, 1.0);
		fragColor = texture(uTexture1, v_textureCoords);

	} else if (v_objNormal.z == -1.0) {
//		fragColor = vec4(0.0, 1.0, 1.0, 1.0);
		fragColor = texture(uTexture2, v_textureCoords);

	} else if (v_objNormal.x == 1.0) {
//		fragColor = vec4(1.0, 1.0, 0.0, 1.0);
		fragColor = texture(uTexture3, v_textureCoords);

	} else if (v_objNormal.y == 1.0) {
//		fragColor = vec4(0.0, 1.0, 0.0, 1.0);
		fragColor = texture(uTexture4, v_textureCoords);

	} else if (v_objNormal.y == -1.0) {
//		fragColor = vec4(0.0, 0.0, 1.0, 1.0);
		fragColor = texture(uTexture5, v_textureCoords);

	} else {
		fragColor = vec4(color, 1.0);
	}

	#ifdef has_alpha
	fragColor.a *= alpha;
	#endif
}
