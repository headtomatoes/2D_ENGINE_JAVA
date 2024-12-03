   #type vertex
   #version 460 core

   layout(location = 0) in vec3 aPos;
   layout(location = 1) in vec4 aCol;
   layout(location = 2) in vec2 aTexCoord;
   layout(location = 3) in float aTexID;

   uniform mat4 uProjection;
   uniform mat4 uView;

   out vec4 fCol;
   out vec2 fTexCoord;
   out float fTexID;

   void main()
   {
      fCol = aCol;
      fTexCoord = aTexCoord;
      fTexID = aTexID;
      gl_Position = uProjection * uView * vec4(aPos, 1.0);
   }
   #type fragment
   #version 460 core

   in vec4 fCol;
   in vec2 fTexCoord;
   in float fTexID;

   uniform sampler2D uTextures[8];

   out vec4 col;

    void main()
    {
       //as long as the fCol = white, the texture's color will be displayed as it is
       //however, we can change the fCol to any color we want to tint the texture like cyan, red, green, etc.
        if(fTexID > 0.0){
            col = fCol * texture(uTextures[int(fTexID)], fTexCoord);
        } else {
            col = fCol;
        }
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