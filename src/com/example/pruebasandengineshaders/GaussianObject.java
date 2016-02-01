package com.example.pruebasandengineshaders;

import com.example.pruebasandengineshaders.ShaderTestActivity.GaussianBlurPass1ShaderProgram;
import com.example.pruebasandengineshaders.ShaderTestActivity.GaussianBlurPass2ShaderProgram;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.render.RenderTexture;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.graphics.drawable.ShapeDrawable.ShaderFactory;
import android.util.Log;

public class GaussianObject{

	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================

	private RenderTexture mRenderTexture1;
	private RenderTexture mRenderTexture2;
	private UncoloredSprite mRenderTextureSprite1;
	private UncoloredSprite mRenderTextureSprite2;
	private TextureManager textureManger;
	
	private Camera mCamera;
	private ShaderProgram gaussian1;
	private ShaderProgram gaussian2;
	private VertexBufferObjectManager vertexBuffer;
	
	private boolean mRenderTexturesInitialized;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public GaussianObject(Camera mCamera, TextureManager textureManger, VertexBufferObjectManager vBoM) {
		this.gaussian1 = GaussianBlurPass1ShaderProgram.getInstance();
		this.gaussian2 = GaussianBlurPass2ShaderProgram.getInstance();
		this.vertexBuffer = vBoM;
		this.textureManger = textureManger;
		this.mCamera = mCamera;
	}
	
	// ===========================================================
	// Getters, Setters, & Public Methods/Functions
	// ===========================================================
	
	public void preDrawn(GLState pGLState){
		if (!mRenderTexturesInitialized) {
			initRenderTexture(pGLState);
			mRenderTexturesInitialized = true;
		}
	}
	
	public void draw(GLState pGLState){
		mRenderTexture1.begin(pGLState, Color.TRANSPARENT);
	}
	
	public void onManagedDrawn(GLState pGLState){
	
		mRenderTexture1.end(pGLState);
		
		mRenderTexture2.begin(pGLState, false, true, Color.TRANSPARENT);
		{
			mRenderTextureSprite1.onDraw(pGLState, mCamera);
		}
		mRenderTexture2.end(pGLState);
		
		pGLState.pushProjectionGLMatrix();
		pGLState.orthoProjectionGLMatrixf(0, mCamera.getSurfaceWidth(), 0, mCamera.getSurfaceHeight(), -1, 1);
		{
			mRenderTextureSprite2.onDraw(pGLState, mCamera);
		}
		pGLState.popProjectionGLMatrix();
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Private/Protected Methods/Functions
	// ===========================================================
	
	private void initRenderTexture(GLState pGLState){
		mRenderTexture1 = new RenderTexture(textureManger, mCamera.getSurfaceWidth(), mCamera.getSurfaceHeight());
		mRenderTexture1.init(pGLState);
		mRenderTextureSprite1 = new UncoloredSprite(0f, 0f, TextureRegionFactory.extractFromTexture(mRenderTexture1), vertexBuffer);
		mRenderTextureSprite1.setShaderProgram(gaussian1);
		
		mRenderTexture2 = new RenderTexture(textureManger, mCamera.getSurfaceWidth(), mCamera.getSurfaceHeight());
		mRenderTexture2.init(pGLState);
		mRenderTextureSprite2 = new UncoloredSprite(0f, 0f, TextureRegionFactory.extractFromTexture(mRenderTexture2), vertexBuffer);
		mRenderTextureSprite2.setShaderProgram(gaussian2);
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================}
}