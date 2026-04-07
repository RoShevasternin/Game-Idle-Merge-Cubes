#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform sampler2D u_texture;
uniform float u_fillPercent;

void main() {

    vec4 tex = texture2D(u_texture, v_texCoords);

    float gray = dot(tex.rgb, vec3(0.299, 0.587, 0.114));
    vec3 grayColor = vec3(gray);

    float fill = u_fillPercent / 100.0;

    float mask = step(v_texCoords.y, fill);

    vec3 color = mix(grayColor, tex.rgb, mask);

    gl_FragColor = vec4(color, tex.a) * v_color;
}