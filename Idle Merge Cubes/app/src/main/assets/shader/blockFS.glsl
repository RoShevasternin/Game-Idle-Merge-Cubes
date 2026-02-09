#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_damage;

// Проста функція для генерації псевдовипадкового шуму
float noise(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

// ... (початок шейдера той самий)

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    vec2 center = vec2(0.5, 0.5);
    vec2 dir = v_texCoords - center;
    float dist = length(dir);

    float angle = atan(dir.y, dir.x);
    float wiggle = sin(angle * 10.0) * 0.05 + sin(angle * 25.0) * 0.02;
    float roughness = noise(v_texCoords * 5.0) * 0.03;

    // Вводимо ліміт (0.7 = 70%)
    float maxRadius = 0.707 * 0.7;

    // Тепер, коли u_damage = 1.0, радіус буде 0.707 * 0.7
    float radius = (u_damage * maxRadius) + wiggle + roughness;

    float mask = smoothstep(radius - 0.01, radius, dist);

    vec4 finalColor = texColor * v_color;
    gl_FragColor = finalColor * mask;
}