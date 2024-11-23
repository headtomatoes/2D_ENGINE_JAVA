   #vertex display
   #version 460 core

   layout(location = 0) in vec3 aPos;
   layout(location = 1) in vec4 aCol;

   out vec4 fCol;

   void main()
   {
      fCol = aCol;
      gl_Position = vec4(aPos, 1.0);
   }
   #fragment display
   #version 460 core

   in vec4 fCol;

   out vec4 col;

    void main()
    {
        col = fCol;
    }

   //Glossary
   // a: attribute
   // u: uniform
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