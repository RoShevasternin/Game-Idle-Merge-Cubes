#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform float u_progress;      // -100..100
uniform float u_startAngle;    // radians
uniform float u_innerEmpty;    // 0..100
uniform float u_roundness;     // 0..1

uniform vec4  u_colorStart;
uniform vec4  u_colorEnd;

#define PI 3.14159265359
#define TWO_PI 6.28318530718

void main()
{
    // ---------------------------------------------------------
    // UV → circle space
    // ---------------------------------------------------------

    vec2 uv = v_texCoords * 2.0 - 1.0;
    float radius = length(uv);

    float outerRadius = 1.0;
    float innerRadius = outerRadius * (clamp(u_innerEmpty, 0.0, 100.0) / 100.0);
    float thickness   = outerRadius - innerRadius;

    // ---------------------------------------------------------
    // angle
    // ---------------------------------------------------------

    float angle = atan(uv.y, uv.x);
    if (angle < 0.0) angle += TWO_PI;

    float startA   = mod(u_startAngle, TWO_PI);
    float maxAngle = (abs(u_progress) / 100.0) * TWO_PI;

    float shifted;

    if (u_progress >= 0.0)
    shifted = mod(angle - startA, TWO_PI);
    else
    shifted = mod(startA - angle, TWO_PI);

    // ---------------------------------------------------------
    // arc check
    // ---------------------------------------------------------

    bool insideArc;
    insideArc = (
    radius <= outerRadius &&
    radius >= innerRadius &&
    shifted <= maxAngle
    );

    // ---------------------------------------------------------
    // round caps
    // ---------------------------------------------------------

    bool insideStartCap = false;
    bool insideEndCap   = false;

    if (u_roundness > 0.001 && maxAngle > 0.001)
    {
        float capRadius = (thickness * 0.5) * u_roundness;
        float midRadius = innerRadius + thickness * 0.5;

        vec2 startCenter = vec2(cos(startA), sin(startA)) * midRadius;

        if (length(uv - startCenter) <= capRadius)
        insideStartCap = true;

        float endA = (u_progress >= 0.0)
        ? startA + maxAngle
        : startA - maxAngle;

        vec2 endCenter = vec2(cos(endA), sin(endA)) * midRadius;

        if (length(uv - endCenter) <= capRadius)
        insideEndCap = true;
    }

    // ---------------------------------------------------------
    // outside → transparent
    // ---------------------------------------------------------

    if (!insideArc && !insideStartCap && !insideEndCap)
    {
        gl_FragColor = vec4(0.0);
        return;
    }

    // ---------------------------------------------------------
    // gradient along arc
    // ---------------------------------------------------------

    float t;

    if (insideStartCap)
    t = 0.0;
    else if (insideEndCap)
    t = 1.0;
    else
    t = (maxAngle > 0.0001)
    ? clamp(shifted / maxAngle, 0.0, 1.0)
    : 0.0;

    vec4 color = mix(u_colorStart, u_colorEnd, t);

    vec4 finalColor = color * v_color;

    // Premultiplied alpha для LibGDX blending (GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
    gl_FragColor = vec4(finalColor.rgb * finalColor.a, finalColor.a);
}