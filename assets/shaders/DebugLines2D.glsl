#type vertex
#version 460 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec3 aCol;

uniform mat4 uProjection;
uniform mat4 uView;

out vec3 fCol;

void main()
{
    fCol = aCol;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}
#type fragment
#version 460 core

in vec3 fCol;

out vec4 col;

void main()
{
    col = vec4(fCol, 1.0);
}
