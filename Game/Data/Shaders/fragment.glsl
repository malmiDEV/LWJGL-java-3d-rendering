#version 330 core
out vec4 frag_color;

in vec2 texCoord;
in float shaderingCoord;

uniform sampler2D textureSampler;

void main() {
    frag_color = texture(textureSampler, texCoord) * shaderingCoord;
}
