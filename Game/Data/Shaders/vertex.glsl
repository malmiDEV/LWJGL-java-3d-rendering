#version 330 core
layout (location = 0) in vec3 vert_coord;
layout (location = 1) in vec2 tex_coord;
layout (location = 2) in float shadering;

out vec2 texCoord;
out float shaderingCoord;

uniform mat4 m, v, p;

void main() {
    gl_Position = p * v * m * vec4(vert_coord, 1.0);
    texCoord = tex_coord;
    shaderingCoord = shadering;
}