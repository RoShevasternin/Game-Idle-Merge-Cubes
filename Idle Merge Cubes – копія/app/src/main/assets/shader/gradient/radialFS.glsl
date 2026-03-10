#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_texture;

uniform vec4 u_startColor;  // Колір початку градієнта
uniform vec4 u_endColor;    // Колір кінця градієнта
uniform vec2 u_resolution;  // Розмір текстури
uniform vec2 u_center;      // Центер
uniform float u_time;       // Час для анімації
uniform float u_cycleTime;  // Тривалість одного циклу анімації
uniform bool u_animate;     // Чи потрібно анімувати градієнт
uniform vec2 u_radius;      // Радіус по осям X та Y
uniform float u_angle;      // Кут обертання у градусах

const float PI     = 3.14159;
const float TWO_PI = 2.0 * PI;

void main() {
    vec2 position = (v_texCoord0 * u_resolution) - u_center;

    // Перетворення кута в радіани
    float angle    = u_angle * PI / 180.0;
    float cosAngle = cos(angle);
    float sinAngle = sin(angle);

    // Обертання координат
    vec2 rotatedPosition = vec2(
        position.x * cosAngle - position.y * sinAngle,
        position.x * sinAngle + position.y * cosAngle
    );

    vec2 scaledPosition = vec2(rotatedPosition.x / u_radius.x, rotatedPosition.y / u_radius.y);
    float dist          = length(scaledPosition);
    dist                = clamp(dist, 0.0, 1.0);

    float animatedFactor = dist;

    if (u_animate) {
        // Анімація кольорів
        float normalizedTime = mod(u_time, u_cycleTime) / u_cycleTime;
        animatedFactor = 0.5 * (1.0 + sin(normalizedTime * TWO_PI + dist * TWO_PI));
    }

    vec4 color    = mix(u_startColor, u_endColor, animatedFactor);
    vec4 texColor = texture2D(u_texture, v_texCoord0);
    gl_FragColor  = color * texColor * v_color;
}