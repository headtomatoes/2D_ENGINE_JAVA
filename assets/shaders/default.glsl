   #type vertex
   #version 460 core

   layout(location = 0) in vec3 aPos;
   layout(location = 1) in vec4 aCol;
   layout(location = 2) in vec2 aTexCoords;

   uniform mat4 uProjection;
   uniform mat4 uView;

   out vec4 fCol;
   out vec2 fTexCoords;
   void main()
   {
      fCol = aCol;
      fTexCoords = aTexCoords;
      gl_Position = uProjection * uView * vec4(aPos, 1.0);
   }
   #type fragment
   #version 460 core

   uniform float uTime;
   uniform sampler2D TEX_SAMPLER;

   in vec4 fCol;
   in vec2 fTexCoords;

   out vec4 col;

    void main()
    {
        vec2 flippedTexCoord = vec2(fTexCoords.x, 1.0 - fTexCoords.y);
        col = texture(TEX_SAMPLER, flippedTexCoord);
    }

   //Glossary
   // a: attribute
   // u: uniform => constant value, variable, that is the same for all vertices
   // global variable , value are set outside of the shader , predefined value, constant draw call
   // EX: transformation matrix (viewMatrix, projMatrix, modelMatrix), light color infomation(Directional lights, point lights, material properties.), texture samplers , global parameters (time-based effects)
   // v: varying
   // f: fragment => for fragment shader

   // in: input
   // out: output

   // t: texture
   // s: sampler
   // c: constant
   // d: define
   // m: macro
   //Pos: position
   //Col: color