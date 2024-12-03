package AnhNe.Utility;

import AnhNe.Components.SpriteSheet;
import Renderer.Shader;
import Renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

// AssetPool class is a utility class that manages the assets in the game. It is a singleton class that stores the shaders, textures, and fonts in the game.
// optimize the loading and management of assets in the game for avoiding garbage collector cause lag spikes.
public class AssetPool {

    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
//    private static Map<String, Texture> fonts = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    // getShader() method is a static method that returns a Shader object from the shaders map if it exists. If the shader does not exist in the map, it creates a new Shader object, compiles it, and adds it to the shaders map.
    public static Shader getShader(String resourceName) {
        File shaderFile = new File(resourceName);
        if(AssetPool.shaders.containsKey(shaderFile.getAbsolutePath())) {
            return AssetPool.shaders.get(shaderFile.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            AssetPool.shaders.put(shaderFile.getAbsolutePath(), shader);
            return shader;
        }
    }

    // getTexture() method is a static method that returns a Texture object from the textures map if it exists. If the texture does not exist in the map, it creates a new Texture object and adds it to the textures map.
    public static Texture getTexture(String resourceName) {
        File textureFile = new File(resourceName);
        if(AssetPool.textures.containsKey(textureFile.getAbsolutePath())) {
            return textures.get(textureFile.getAbsolutePath());
        } else {
            Texture texture = new Texture(resourceName);
            AssetPool.textures.put(textureFile.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpriteSheet(String resourceName, SpriteSheet spriteSheet) {
        // add sprite sheet
        File spriteSheetFile = new File(resourceName);

        if(!AssetPool.spriteSheets.containsKey(spriteSheetFile.getAbsolutePath())) {
            AssetPool.spriteSheets.put(spriteSheetFile.getAbsolutePath(), spriteSheet);
        }
    }

    public static SpriteSheet getSpriteSheet(String resourceName) {
        File spriteSheetFile = new File(resourceName);
        if(!AssetPool.spriteSheets.containsKey(spriteSheetFile.getAbsolutePath())) {
            assert false : "ERROR: Sprite sheet not found in AssetPool" + resourceName + " 'and it has not been loaded yet"; ;
        }
        return AssetPool.spriteSheets.getOrDefault(spriteSheetFile.getAbsolutePath(), null);
    }
}
