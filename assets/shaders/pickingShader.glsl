#type vertex
#version 460 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 aCol;
layout(location = 2) in vec2 aTexCoord;
layout(location = 3) in float aTexID;
layout(location = 4) in float aEntityID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fCol;
out vec2 fTexCoord;
out float fTexID;
out float fEntityID;

void main()
{
    fCol = aCol;
    fTexCoord = aTexCoord;
    fTexID = aTexID;
    fEntityID = aEntityID;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}
#type fragment
#version 460 core

in vec4 fCol;
in vec2 fTexCoord;
in float fTexID;
in float fEntityID;

uniform sampler2D uTextures[8];

out vec3 col;

void main()
{
    vec4 texColor = vec4(1, 1, 1, 1);

    if(fTexID > 0.0){
        int id = int(fTexID);
        texColor = fCol * texture(uTextures[id], fTexCoord);
    }

    //Picking color
    if(texColor.a < 0.5){
        discard;
    }

    col = vec3(fEntityID , fEntityID, fEntityID);
}