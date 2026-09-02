#version 330

#moj_import <holymoderation:common.glsl>

in vec2 FragCoord;
in vec4 FragColor;

layout(std140) uniform RectangleUniforms {
   vec4 Radius;
   vec2 Size;
   float Smoothness;
};

out vec4 OutColor;

void main() {
    float alpha = ralpha(Size, FragCoord, Radius, Smoothness);
    vec4 color = vec4(FragColor.rgb, FragColor.a * alpha);

    if (color.a == 0.0) { // alpha test
        discard;
    }

    OutColor = color;
}