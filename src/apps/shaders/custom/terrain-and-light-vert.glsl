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

#define C_ZERO 0.0
#define C_ONE 1.0

// texture containing elevation data
//uniform sampler2D heightMap;
uniform sampler2D bumpmap;

uniform float displace_multiply;

in vec3 position;

in vec3 normal;

in vec2 uv; // available when texture maps (bumpmap, colormap, ...) are used on object

out vec3 v_normal;

out vec2 v_textureCoords;

//struct direction_light {
	uniform vec3 light_direction; // normalized direction in eye
	uniform vec3 light_halfplane; // normalized half-plane vector
	uniform vec4 light_ambient_color;
	uniform vec4 light_diffuse_color;
	uniform vec4 light_specular_color;
//};

//struct material_props {
	uniform vec4 material_ambient_color;
	uniform vec4 material_diffuse_color;
	uniform vec4 material_specular_color;
	uniform float material_specular_exponent;
//};

//uniform material_props material;
//uniform direction_light light;

out vec4 v_color;

vec4 directional_light_color (vec3 normal) {
	vec4 computed_color = vec4(C_ZERO, C_ZERO, C_ZERO, C_ZERO);
	float ndotL; // dot product of normal & light direction
	float ndotH; // dot product of nomral and & half-plane vector

	ndotL = max(C_ZERO, dot(normal, light_direction));
	ndotH = max(C_ZERO, dot(normal, light_halfplane));
	computed_color += light_ambient_color * material_ambient_color;
	computed_color += ndotL * light_diffuse_color * material_diffuse_color;

	if (ndotH > C_ZERO) {
		computed_color += pow(ndotH, material_specular_exponent) * material_specular_color * light_specular_color;
	}

	return computed_color;
}

void main() {
	float displacement = texture(bumpmap, uv).r;

	vec3 displace_along_normal = vec3(normal * displacement);

	vec3 displaced_position = position + (displace_multiply * displace_along_normal);

	// NEED TO CALCULATE NORMAL DISPLACEMENT BY SAMPLING NEIGHBOR UV's
	v_normal = normal + displacement;

	v_textureCoords = uv;

	v_color = material_diffuse_color;

	gl_Position = PROJECTION * VIEW * MODEL * vec4(displaced_position, C_ONE);
}
