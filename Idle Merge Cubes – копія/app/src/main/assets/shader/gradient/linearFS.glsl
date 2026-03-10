#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_texture;

uniform vec3 u_startColor;  // Колір початку градієнта
uniform vec3 u_endColor;    // Колір кінця градієнта
uniform float u_angle;      // Кут у градусах
uniform float u_time;       // Час для анімації
uniform float u_cycleTime;  // Тривалість одного циклу анімації в секундах
uniform bool u_animate;     // Чи потрібно анімувати градієнт

const float PI     = 3.14159;
const float TWO_PI = 2.0 * PI;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord0);

    // Обчислення напряму градієнта з кута
    float rad            = radians(u_angle);
    vec2 gradientDir     = vec2(cos(rad), sin(rad));
    float gradientFactor = dot(v_texCoord0, gradientDir);

    float animatedFactor = gradientFactor;

    if (u_animate) {
        // Нормалізація часу для циклу анімації
        float normalizedTime = mod(u_time, u_cycleTime) / u_cycleTime;
        animatedFactor       = 0.5 * (1.0 + sin(normalizedTime * TWO_PI + gradientFactor * PI));
    }

    // Обчислення проміжного кольору
    vec3 gradientColor = mix(u_startColor, u_endColor, animatedFactor);

    // Застосування кольору текстури та кольору градієнта
    vec4 resultColor = vec4(gradientColor, texColor.a);

    gl_FragColor = resultColor * v_color;
}