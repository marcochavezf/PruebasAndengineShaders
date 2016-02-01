package com.example.pruebasandengineshaders;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationByModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.shader.PositionTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.render.RenderTexture;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderTestActivity extends BaseGameActivity {
	
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	
	private Camera mCamera;
    private ITexture mTexture;
    private ITextureRegion mTextureRegion;
    
	private boolean mRenderTexturesInitialized = false;
	private RenderTexture mRenderTexture1;
	private Sprite mRenderTextureSprite1;
	
	private RenderTexture mRenderTexture2;
	private Sprite mRenderTextureSprite2;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, WIDTH, HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(WIDTH, HEIGHT), this.mCamera);
		return engineOptions;
	}
	
	@Override
    public Engine onCreateEngine(final EngineOptions pEngineOptions) {
        return new LimitedFPSEngine(pEngineOptions, 60) {
        	
        	@Override
        	public void onDrawFrame(GLState pGLState)
        			throws InterruptedException {
				
				
				
				super.onDrawFrame(pGLState);
				
//				Log.d("Debugging", "llamanda 3");
//				
//				mRenderTexture2.begin(pGLState, false, true, Color.TRANSPARENT);
//				{
//					mRenderTextureSprite1.onDraw(pGLState, mCamera);
//				}
//				mRenderTexture2.end(pGLState);
//				
//				Log.d("Debugging", "llamanda 4");
//				
//				pGLState.pushProjectionGLMatrix();
//				pGLState.orthoProjectionGLMatrixf(0, mCamera.getSurfaceWidth(), 0, mCamera.getSurfaceHeight(), -1, 1);
//				{
//					mRenderTextureSprite2.onDraw(pGLState, mCamera);
//				}
//				pGLState.popProjectionGLMatrix();
//				
//				Log.d("Debugging", "llamanda 5");
        	}
        	
			
        };
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)	throws Exception {
        try {
			this.mTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return getAssets().open("gfx/mona_lisa.jpg");
				}
			});

			this.mTexture.load();
			this.mTextureRegion = TextureRegionFactory.extractFromTexture(this.mTexture);
		} catch (IOException e) {
			Debug.e(e);
		}
        
        this.getShaderProgramManager().loadShaderProgram(GaussianBlurPass1ShaderProgram.getInstance());
        this.getShaderProgramManager().loadShaderProgram(GaussianBlurPass2ShaderProgram.getInstance());
        this.getShaderProgramManager().loadShaderProgram(RadialBlurShaderProgram.getInstance());
        this.getShaderProgramManager().loadShaderProgram(ShaderProgramA.getInstance());
        
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {        
			this.mEngine.registerUpdateHandler(new FPSLogger());
	        final Scene scene = new Scene();
	        scene.setBackground(new Background(0.0f, 0.0f, 0.0f));
	        getEngine().registerUpdateHandler(new FPSLogger());
	        pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}
	
	private void initRenderTexture(GLState pGLState) {
		mRenderTexture1 = new RenderTexture(ShaderTestActivity.this.getTextureManager(), mCamera.getSurfaceWidth(), mCamera.getSurfaceHeight());
		mRenderTexture1.init(pGLState);
		mRenderTextureSprite1 = new UncoloredSprite(0f, 0f, TextureRegionFactory.extractFromTexture(mRenderTexture1), getVertexBufferObjectManager());
		mRenderTextureSprite1.setShaderProgram(GaussianBlurPass1ShaderProgram.getInstance());
		
		mRenderTexture2 = new RenderTexture(ShaderTestActivity.this.getTextureManager(), mCamera.getSurfaceWidth(), mCamera.getSurfaceHeight());
		mRenderTexture2.init(pGLState);
		mRenderTextureSprite2 = new UncoloredSprite(0f, 0f, TextureRegionFactory.extractFromTexture(mRenderTexture2), getVertexBufferObjectManager());
		mRenderTextureSprite2.setShaderProgram(GaussianBlurPass2ShaderProgram.getInstance());
//		mRenderTextureSprite2.setShaderProgram(ShaderProgramA.getInstance());
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		
		Sprite mSprite = new Sprite(100, 115, this.mTextureRegion, getVertexBufferObjectManager()) {			
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				if (!mRenderTexturesInitialized) {
					initRenderTexture(pGLState);
					mRenderTexturesInitialized = true;
				}
				super.preDraw(pGLState, pCamera);
			}
			
			@Override
			protected void draw(GLState pGLState, Camera pCamera) {			
				
				
				mRenderTexture1.begin(pGLState, Color.TRANSPARENT);
				{
					super.draw(pGLState, pCamera);
				}
				
			}
			
			@Override
			protected void onManagedDraw(GLState pGLState, Camera pCamera) {
				super.onManagedDraw(pGLState, pCamera);
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
		};
		pScene.attachChild(mSprite);
//		this.mSprite.registerEntityModifier(
//		new LoopEntityModifier(
//				new RotationByModifier(3.0f, 180)));
		
		final GaussianObject gaussian = new GaussianObject(mCamera, getTextureManager(), getVertexBufferObjectManager());
		Sprite sprite2 = new Sprite(0, 400, this.mTextureRegion, getVertexBufferObjectManager()){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				gaussian.preDrawn(pGLState);
				super.preDraw(pGLState, pCamera);
			}
			
			@Override
			protected void draw(GLState pGLState, Camera pCamera) {
				gaussian.draw(pGLState);
				super.draw(pGLState, pCamera);
			}
			
			@Override
			protected void onManagedDraw(GLState pGLState, Camera pCamera) {
				super.onManagedDraw(pGLState, pCamera);
				gaussian.onManagedDrawn(pGLState);
			}
		};
//		sprite2.setShaderProgram(RadialBlurShaderProgram.getInstance());
		pScene.attachChild(sprite2);
		
		final GaussianObject gaussian3 = new GaussianObject(mCamera, getTextureManager(), getVertexBufferObjectManager());
		Sprite sprite3 = new Sprite(0, 0, this.mTextureRegion, getVertexBufferObjectManager()){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				gaussian3.preDrawn(pGLState);
				super.preDraw(pGLState, pCamera);
			}
			
			@Override
			protected void draw(GLState pGLState, Camera pCamera) {
				gaussian3.draw(pGLState);
				super.draw(pGLState, pCamera);
			}
			
			@Override
			protected void onManagedDraw(GLState pGLState, Camera pCamera) {
				super.onManagedDraw(pGLState, pCamera);
				gaussian3.onManagedDrawn(pGLState);
			}
		};
		pScene.attachChild(sprite3);
		
		Sprite sprite4 = new Sprite(240, 400, this.mTextureRegion, getVertexBufferObjectManager()){
			@Override
			protected void applyRotation(final GLState pGLState) {
				final float rotation = this.mRotation;

				if(rotation != 0) {
					final float rotationCenterX = this.mRotationCenterX;
					final float rotationCenterY = this.mRotationCenterY;

					pGLState.translateModelViewGLMatrixf(rotationCenterX, rotationCenterY, 0);
					/* Note we are applying rotation around the y-axis and not the z-axis anymore! */
					pGLState.rotateModelViewGLMatrixf(rotation, 0, 1, 0);
					pGLState.translateModelViewGLMatrixf(-rotationCenterX, -rotationCenterY, 0);
				}
			}
		};
		
		sprite4.setShaderProgram(ShaderProgramA.getInstance());
		pScene.attachChild(sprite4);
//		sprite4.registerEntityModifier(new LoopEntityModifier(new RotationModifier(6, 0, 360)));
//		sprite4.registerEntityModifier(
//		new LoopEntityModifier(
//				new RotationByModifier(3.0f, 180)));
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
public static class GaussianBlurPass1ShaderProgram extends ShaderProgram {
		
		private static GaussianBlurPass1ShaderProgram instance;
		
		public static GaussianBlurPass1ShaderProgram getInstance() {
			if (instance == null) instance = new GaussianBlurPass1ShaderProgram();
			return instance;
		}
	
		public static final String FRAGMENTSHADER = 
				"precision lowp float;\n" +

	            "uniform sampler2D " + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ";\n" +
	            "varying mediump vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +

				"const float blurSize = 2.0/" + (WIDTH-1) + ".0;	\n" +
 
				"void main()	\n" +
				"{				\n" +
				"	vec4 sum = vec4(0.0);	\n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x - 4.0*blurSize, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.05; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x - 3.0*blurSize, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.09; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x - 2.0*blurSize, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.12; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x - blurSize, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.15; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.16; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x + blurSize, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.15; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x + 2.0*blurSize, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.12;	\n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x + 3.0*blurSize, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.09;	\n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x + 4.0*blurSize, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.05;	\n" +
				"	gl_FragColor = sum;	\n" +
				"}						\n";
		private GaussianBlurPass1ShaderProgram() {
			super(PositionTextureCoordinatesShaderProgram.VERTEXSHADER, FRAGMENTSHADER);
		}
		
		
        public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
        public static int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
        
        @Override
        protected void link(final GLState pGLState) throws ShaderProgramLinkException {
            GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION);
            GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);

            super.link(pGLState);

            GaussianBlurPass1ShaderProgram.sUniformModelViewPositionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
            GaussianBlurPass1ShaderProgram.sUniformTexture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);

        }
        
        @Override
        public void bind(final GLState pGLState, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
            GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

            super.bind(pGLState, pVertexBufferObjectAttributes);
            
            GLES20.glUniformMatrix4fv(GaussianBlurPass1ShaderProgram.sUniformModelViewPositionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
            GLES20.glUniform1i(GaussianBlurPass1ShaderProgram.sUniformTexture0Location, 0);
        }

      
        @Override
        public void unbind(final GLState pGLState) throws ShaderProgramException {
            GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

            super.unbind(pGLState);
        }
	}
	
public static class GaussianBlurPass2ShaderProgram extends ShaderProgram {
		
		private static GaussianBlurPass2ShaderProgram instance;
		
		public static GaussianBlurPass2ShaderProgram getInstance() {
			if (instance == null) instance = new GaussianBlurPass2ShaderProgram();
			return instance;
		}
				
		public static final String FRAGMENTSHADER =
				"precision lowp float;\n" +

	            "uniform sampler2D " + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ";\n" +
	            "varying mediump vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +

				"const float blurSize = 2.0/" + (HEIGHT-1) + ".0;	\n" +
 
				"void main()	\n" +
				"{				\n" +
				"	vec4 sum = vec4(0.0);	\n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y - 4.0*blurSize)) * 0.05; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y - 3.0*blurSize)) * 0.09; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y - 2.0*blurSize)) * 0.12; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y - blurSize)) * 0.15; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y)) * 0.16; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y + blurSize)) * 0.15; \n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y + 2.0*blurSize)) * 0.12;	\n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y + 3.0*blurSize)) * 0.09;	\n" +
				"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", vec2(" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".x, " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ".y + 4.0*blurSize)) * 0.05;	\n" +
				"	gl_FragColor = sum;	\n" +
				"}						\n";
				
		private GaussianBlurPass2ShaderProgram() {
			super(PositionTextureCoordinatesShaderProgram.VERTEXSHADER,
					FRAGMENTSHADER);
		}
		
        public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
        public static int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
        
        @Override
        protected void link(final GLState pGLState) throws ShaderProgramLinkException {
            GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION);
            GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);

            super.link(pGLState);

            GaussianBlurPass2ShaderProgram.sUniformModelViewPositionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
            GaussianBlurPass2ShaderProgram.sUniformTexture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);

        }
        
        @Override
        public void bind(final GLState pGLState, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
            GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

            super.bind(pGLState, pVertexBufferObjectAttributes);
            
            GLES20.glUniformMatrix4fv(GaussianBlurPass2ShaderProgram.sUniformModelViewPositionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
            GLES20.glUniform1i(GaussianBlurPass2ShaderProgram.sUniformTexture0Location, 0);
        }

      
        @Override
        public void unbind(final GLState pGLState) throws ShaderProgramException {
            GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

            super.unbind(pGLState);
        }	
	}
	
	public static class RadialBlurShaderProgram extends ShaderProgram {
		// ===========================================================
		// Constants
		// ===========================================================

		private static RadialBlurShaderProgram INSTANCE;

		public static final String VERTEXSHADER =
			"uniform mat4 " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + ";\n" +
			"attribute vec4 " + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" +
			"attribute vec2 " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
			"varying vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +
			"void main() {\n" +
			"	" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " = " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
			"	gl_Position = " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + " * " + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" +
			"}";

		private static final String UNIFORM_RADIALBLUR_CENTER = "u_radialblur_center";

		public static final String FRAGMENTSHADER =
			"precision lowp float;\n" +

			"uniform sampler2D " + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ";\n" +
			"varying mediump vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +

			"uniform vec2 " + RadialBlurShaderProgram.UNIFORM_RADIALBLUR_CENTER + ";\n" +

			"const float sampleShare = (1.0 / 11.0);\n" +
			"const float sampleDist = 1.0;\n" +
			"const float sampleStrength = 1.25;\n" +

			"void main() {\n" +
			/* The actual (unburred) sample. */
			"	vec4 color = texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ");\n" +

			/* Calculate direction towards center of the blur. */
			"	vec2 direction = " + RadialBlurShaderProgram.UNIFORM_RADIALBLUR_CENTER + " - " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +

			/* Calculate the distance to the center of the blur. */
			"	float distance = sqrt(direction.x * direction.x + direction.y * direction.y);\n" +

			/* Normalize the direction (reuse the distance). */
			"	direction = direction / distance;\n" +

			"	vec4 sum = color * sampleShare;\n" +
			/* Take 10 additional samples along the direction towards the center of the blur. */
			"	vec2 directionSampleDist = direction * sampleDist;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.08 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.05 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.03 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.02 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.01 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.01 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.02 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.03 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.05 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.08 * directionSampleDist) * sampleShare;\n" +

			/* Weighten the blur effect with the distance to the center of the blur (further out is blurred more). */
			"	float t = sqrt(distance) * sampleStrength;\n" +
			"	t = clamp(t, 0.0, 1.0);\n" + // 0 <= t >= 1

			/* Blend the original color with the averaged pixels. */
			"	gl_FragColor = mix(color, sum, t);\n" +
			"}";

		// ===========================================================
		// Fields
		// ===========================================================

		public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
		public static int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
		public static int sUniformRadialBlurCenterLocation = ShaderProgramConstants.LOCATION_INVALID;

		// ===========================================================
		// Constructors
		// ===========================================================

		private RadialBlurShaderProgram() {
			super(RadialBlurShaderProgram.VERTEXSHADER, RadialBlurShaderProgram.FRAGMENTSHADER);
		}

		public static RadialBlurShaderProgram getInstance() {
			if(RadialBlurShaderProgram.INSTANCE == null) {
				RadialBlurShaderProgram.INSTANCE = new RadialBlurShaderProgram();
			}
			return RadialBlurShaderProgram.INSTANCE;
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		protected void link(final GLState pGLState) throws ShaderProgramLinkException {
			GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION);
			GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);

			super.link(pGLState);

			RadialBlurShaderProgram.sUniformModelViewPositionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
			RadialBlurShaderProgram.sUniformTexture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);

			RadialBlurShaderProgram.sUniformRadialBlurCenterLocation = this.getUniformLocation(RadialBlurShaderProgram.UNIFORM_RADIALBLUR_CENTER);
		}

		@Override
		public void bind(final GLState pGLState, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
			GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

			super.bind(pGLState, pVertexBufferObjectAttributes);

			GLES20.glUniformMatrix4fv(RadialBlurShaderProgram.sUniformModelViewPositionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
			GLES20.glUniform1i(RadialBlurShaderProgram.sUniformTexture0Location, 0);
		}

		@Override
		public void unbind(final GLState pGLState) throws ShaderProgramException {
			GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

			super.unbind(pGLState);
		}

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
	
	
	
public static class ShaderProgramA extends ShaderProgram {
		
		private static ShaderProgramA instance;
		private float time;
		
		public static ShaderProgramA getInstance() {
			if (instance == null) instance = new ShaderProgramA();
			return instance;
		}
				
		public static final String VERTEXSHADER =
				"precision mediump float;\n" +
                "uniform	mat4 " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + ";\n" +
                "attribute	vec4 " + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" +
                "attribute	vec2 " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
                "varying 	vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +
                "uniform	float time;\n" + 
//                "uniform	float rnd;\n" + 
                "void main() {\n" +
                "       " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " = " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
                "       " + "vec4 v = vec4("+ShaderProgramConstants.ATTRIBUTE_POSITION+");\n"+
                "       " + "v.y = v.y + sin(.5 * v.x + time * 0.0005) * 15.0;\n"+
                "       " + "v.x = v.x + sin(.5 * v.y + time * 0.0005) * 15.0;\n" +
                //"       " + ShaderProgramConstants.ATTRIBUTE_POSITION + ".z = 0.0;\n" +
                "       gl_Position = " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + " * v;\n" +
                "}";
		
		public static final String FRAGMENTSHADER = 
			    "precision mediump float;           \n" +
			    "uniform vec4 theColor;             \n" +
			    "void main()                                        \n" +
			    "{                                                  \n" +
			    "   gl_FragColor = theColor;        \n" +
			    "}";
		
		private ShaderProgramA() {
			super(VERTEXSHADER, PositionTextureCoordinatesShaderProgram.FRAGMENTSHADER);
			this.time = 0.0f;
		}
		
		
        public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
        public static int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
        public static int sUniformTime = ShaderProgramConstants.LOCATION_INVALID;
//        public static int sUniformRnd = ShaderProgramConstants.LOCATION_INVALID;
        
        
        @Override
        protected void link(final GLState pGLState) throws ShaderProgramLinkException {
            GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION);
            GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);

            super.link(pGLState);

            ShaderProgramA.sUniformModelViewPositionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
            ShaderProgramA.sUniformTexture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);
            ShaderProgramA.sUniformTime = this.getUniformLocation("time");
//            ShaderProgramA.sUniformRnd = this.getUniformLocation("rnd");
        }
        
        @Override
        public void bind(final GLState pGLState, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
            GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

            super.bind(pGLState, pVertexBufferObjectAttributes);
            
            GLES20.glUniformMatrix4fv(ShaderProgramA.sUniformModelViewPositionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
            GLES20.glUniform1i(ShaderProgramA.sUniformTexture0Location, 0);
            GLES20.glUniform1f(ShaderProgramA.sUniformTime, time += 10f);
//            GLES20.glUniform1f(ShaderProgramA.sUniformRnd, (float) (Math.random()*10));
        }

      
        @Override
        public void unbind(final GLState pGLState) throws ShaderProgramException {
            GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

            super.unbind(pGLState);
        }
	}
	
}
