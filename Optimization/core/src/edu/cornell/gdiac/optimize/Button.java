package edu.cornell.gdiac.optimize;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;

public class Button {
    private float width;
    private float height;
    private String text;
    private String connectedFile = null; // String of JSON file name connected to it
    private Vector2 position; // screen position
    private int fontSize; // size of the font to draw
    //        private int exitCode;
    private boolean touched = false;
    private Texture texture;
    private BitmapFont font;
    private float imgScale; //TODO: SWITCH OUT IN DRAW
    private  Color color;
    private boolean levelButton;

    public Button(float w, float h, boolean lB, String t, BitmapFont f, String cF, Vector2 pos, int fontS, Texture texture) {
        width = w;
        height = h;
        text = t;
        connectedFile = cF;
        position = pos;
        fontSize = fontS;
        font = f;
//            exitCode = exitc;
        this.texture = texture;
        color =  Color.WHITE;
        levelButton = lB;
    }

    public Button(float w, float h, boolean lB, String t, BitmapFont f, String cF, Vector2 pos, int fontS, Texture texture, Color c) {
        width = w;
        height = h;
        text = t;
        connectedFile = cF;
        position = pos;
        fontSize = fontS;
        font = f;
//            exitCode = exitc;
        color = c;
        this.texture = texture;
        levelButton = lB;
    }

    public boolean isLevelButton(){
        return levelButton;
    }

    public String getConnectedFile() {
        return connectedFile;
    }

    public String getText() { return text; }

    public boolean getTouched() {
        return touched;
    }

    public void setTouched(boolean b) {
        touched = b;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void draw(GameCanvas canvas) {
        Color c = color;
        Color fontC;
        if (color == null){
            c = touched ? Color.GRAY : Color.WHITE;
            fontC = c;
        } else {
            fontC = Color.WHITE;
        }
        float x = texture.getWidth()/2.0f;
        float y = texture.getHeight()/2.0f;
//            canvas.draw(texture, c, x, y, position.x , position.y, 0, 1, 1);
        canvas.draw(texture, position.x , position.y, width, height, c);

        GlyphLayout layout = new GlyphLayout(font,text);
//            float fontPosX = position.x + width/2 - text.length()*4/2f ;//+ texture.getWidth()/2;
//            float fontPosY = position.y + height/2;
        float fontPosX;//+ texture.getWidth()/2;
        float fontPosY = (position.y + height/2 + layout.height/2f);
        if(levelButton){
            fontPosX = (position.x + width/2 - layout.width/2f) ;
            fontPosY = (position.y + height/1.6f + layout.height/2f);

        }else{
            fontPosX = (position.x + width/2.5f - layout.width/2f);
        }
        if (text == "Locked"){
            fontPosX = position.x + (width - layout.width)/2f;
            fontPosY = fontPosY - font.getCapHeight() / 2;
        }

        if(levelButton || text == "Locked"){
                canvas.drawText(text, font, fontPosX, fontPosY, Color.WHITE);
        }else {
            canvas.drawText(text, font, fontPosX, fontPosY, Color.BLACK);
        }//should have color attribute in object?
    }


}
