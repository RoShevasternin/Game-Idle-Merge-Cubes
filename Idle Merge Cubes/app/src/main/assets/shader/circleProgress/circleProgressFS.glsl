//#ifdef GL_ES
//precision mediump float;
//#endif
//
//varying vec2 v_texCoords;
//varying vec4 v_color;
//
//uniform float u_progress;
//uniform float u_startAngle;
//uniform float u_innerEmpty;
//uniform float u_roundness;
//uniform vec4  u_colorStart;
//uniform vec4  u_colorEnd;
//
//#define PI 3.14159265359
//#define TWO_PI 6.28318530718
//
//void main() {
//
//    vec2 uv = v_texCoords * 2.0 - 1.0;
//    float radius = length(uv);
//
//    float outerRadius = 1.0;
//    float innerRadius = outerRadius * clamp(u_innerEmpty, 0.0, 1.0);
//    float thickness = outerRadius - innerRadius;
//
//    float angle = atan(uv.y, uv.x);
//    if (angle < 0.0) angle += TWO_PI;
//
//    float startAngle = u_startAngle;
//    if (startAngle < 0.0) startAngle += TWO_PI;
//
//    float maxAngle = abs(u_progress) / 100.0 * TWO_PI;
//
//    float shifted;
//    if (u_progress >= 0.0) {
//        shifted = angle - startAngle;
//        if (shifted < 0.0) shifted += TWO_PI;
//    } else {
//        shifted = startAngle - angle;
//        if (shifted < 0.0) shifted += TWO_PI;
//    }
//
//    bool insideArc;
//    insideArc = (radius <= outerRadius);
//    insideArc = insideArc && (radius >= innerRadius);
//    insideArc = insideArc && (shifted <= maxAngle);
//
//    bool insideStartCap = false;
//    bool insideEndCap   = false;
//
//    if (u_roundness > 0.001 && maxAngle > 0.0001) {
//
//        float capRadius = thickness * u_roundness * 0.5;
//
//        vec2 startDir = vec2(cos(startAngle), sin(startAngle));
//        vec2 startCenter = startDir * (innerRadius + thickness * 0.5);
//
//        float endAngle = (u_progress >= 0.0)
//        ? startAngle + maxAngle
//        : startAngle - maxAngle;
//
//        vec2 endDir = vec2(cos(endAngle), sin(endAngle));
//        vec2 endCenter = endDir * (innerRadius + thickness * 0.5);
//
//        float dStart = length(uv - startCenter);
//        float dEnd   = length(uv - endCenter);
//
//        if (dStart <= capRadius) insideStartCap = true;
//        if (dEnd   <= capRadius) insideEndCap   = true;
//    }
//
//    if (!insideArc && !insideStartCap && !insideEndCap)
//    discard;
//
//    float t;
//
//    if (insideStartCap) {
//        t = 0.0;
//    }
//    else if (insideEndCap) {
//        t = 1.0;
//    }
//    else {
//        t = clamp(shifted / maxAngle, 0.0, 1.0);
//    }
//
//    vec4 color = mix(u_colorStart, u_colorEnd, t);
//
//    color.rgb *= color.a;
//    gl_FragColor = color * v_color;
//}


#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform float u_progress;      // 0..100 (або від'ємні для іншого напрямку)
uniform float u_startAngle;    // радіани
uniform float u_innerEmpty;    // 0..100 (відсоток "дірки")
uniform float u_roundness;     // 0..1 (коефіцієнт заокруглення)
uniform vec4  u_colorStart;
uniform vec4  u_colorEnd;

#define PI 3.14159265359
#define TWO_PI 6.28318530718

void main() {
    vec2 uv = v_texCoords * 2.0 - 1.0;
    float radius = length(uv);

    float outerRadius = 1.0;
    // ТЕПЕР ТУТ ДІЛИМО НА 100
    float innerRadius = outerRadius * (clamp(u_innerEmpty, 0.0, 100.0) / 100.0);
    float thickness = outerRadius - innerRadius;

    float angle = atan(uv.y, uv.x);
    if (angle < 0.0) angle += TWO_PI;

    float startA = mod(u_startAngle, TWO_PI);
    float maxAngle = (abs(u_progress) / 100.0) * TWO_PI;

    float shifted;
    if (u_progress >= 0.0) {
        shifted = mod(angle - startA, TWO_PI);
    } else {
        shifted = mod(startA - angle, TWO_PI);
    }

    // Перевірка на основну дугу
    bool insideArc;
    insideArc = (radius <= outerRadius && radius >= innerRadius && shifted <= maxAngle);

    bool insideStartCap = false;
    bool insideEndCap   = false;

    if (u_roundness > 0.001 && maxAngle > 0.001) {
        // Радіус шапки — це половина товщини кільця * коефіцієнт заокруглення
        float capRadius = (thickness * 0.5) * u_roundness;
        float midRadius = innerRadius + (thickness * 0.5);

        // Стартова шапка
        vec2 startCenter = vec2(cos(startA), sin(startA)) * midRadius;
        if (length(uv - startCenter) <= capRadius) insideStartCap = true;

        // Кінцева шапка
        float endA = (u_progress >= 0.0) ? startA + maxAngle : startA - maxAngle;
        vec2 endCenter = vec2(cos(endA), sin(endA)) * midRadius;
        if (length(uv - endCenter) <= capRadius) insideEndCap = true;
    }

    if (!insideArc && !insideStartCap && !insideEndCap) discard;

    // Розрахунок градієнта
    float t;
    if (insideStartCap) t = 0.0;
    else if (insideEndCap) t = 1.0;
    else t = clamp(shifted / maxAngle, 0.0, 1.0);

    vec4 color = mix(u_colorStart, u_colorEnd, t);
    color.rgb *= color.a; // Premultiplied
    gl_FragColor = color * v_color;
}