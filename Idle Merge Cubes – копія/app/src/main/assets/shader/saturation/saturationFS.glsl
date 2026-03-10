#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_saturation;
uniform float u_alpha;

varying vec2 v_texCoords;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);

    // Convert to grayscale
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));

    // Mix between the grayscale color and the original color
    vec3 adjustedColor = mix(vec3(gray), color.rgb, u_saturation);

    gl_FragColor = vec4(adjustedColor, color.a * u_alpha);
}