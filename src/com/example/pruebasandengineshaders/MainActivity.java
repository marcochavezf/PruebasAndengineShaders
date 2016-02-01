package com.example.pruebasandengineshaders;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Mesh;
import org.andengine.entity.primitive.vbo.HighPerformanceMeshVertexBufferObject;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;


/**
 * //touch to modify sin height 
 * 
 * @author Pawe³ Plewa 
 *
 */

public class MainActivity extends SimpleBaseGameActivity {
	
	public static final float CAMERA_WIDTH = 1280;
	public static final float CAMERA_HEIGHT = 800;
	private SmoothCamera mCamera;
	private Scene mScene;
	public float mTouchX = 110;
	public float mTouchY = 220;
	
	float[] mHeightOffsetCurrent;

	Mesh pMesh;
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		Engine pEngine =  new LimitedFPSEngine(pEngineOptions, 60);
		return pEngine;
	}	
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,100,100,0.5f);
		EngineOptions pOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);
		return pOptions;
	}


	@Override
	protected void onCreateResources() {
		 
	}

	@Override
	protected Scene onCreateScene() {
		this.getEngine().registerUpdateHandler(new FPSLogger());
		
		mScene = new Scene(){
			public boolean onSceneTouchEvent(org.andengine.input.touch.TouchEvent pSceneTouchEvent) {
				super.onSceneTouchEvent(pSceneTouchEvent);
				mTouchX = pSceneTouchEvent.getX();
				mTouchY = pSceneTouchEvent.getY();
				return true;
			};
		};
		
		mScene.setBackground(new Background(1, 1, 1));
		
		mCamera.setZoomFactorDirect(0.9f); 
		
		this.createMesh(45); //45 top triangles from which terrain will be build 
		
		mScene.attachChild(pMesh);	
		
		return mScene;
	}
	
	/**
	 * number of triangles to fill entire camera width 
	 * @param pTrianglesCount 
	 * @return
	 */
	private Mesh createMesh(final int pTrianglesCount) {
		final int pSpeed 			= 20;
		final int pVertexCount	 	= Mesh.VERTEX_SIZE * pTrianglesCount * 3; 	
		final float pColor 			= new Color(0f,0f,0f).getABGRPackedFloat();
		final float pSegmentWidth 	= CAMERA_WIDTH/pTrianglesCount;
		final float[] pBufferData 	= new float[pVertexCount];	
		
		mHeightOffsetCurrent = new float[pVertexCount];		
		
		//create triangles 
		//   A--B
		//    \ |
		//     \|
		//      C
		//
		int i = 0;
		float x = 0f;
		final float pInitialHeight = 400;
		for (int triangleIndex = 0;triangleIndex<pTrianglesCount;triangleIndex++){
			 //first triangle 
			 pBufferData[(i * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_X] = x;
			 pBufferData[(i * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_Y] = pInitialHeight;
			 pBufferData[(i * Mesh.VERTEX_SIZE) + Mesh.COLOR_INDEX] = pColor;
			 
			 pBufferData[((i+1) * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_X] = x+pSegmentWidth;
			 pBufferData[((i+1) * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_Y] = pInitialHeight;
			 pBufferData[((i+1) * Mesh.VERTEX_SIZE) + Mesh.COLOR_INDEX] = pColor;
			 
			 pBufferData[((i+2) * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_X] = x+pSegmentWidth;
			 pBufferData[((i+2) * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_Y] = 0;	
			 pBufferData[((i+2) * Mesh.VERTEX_SIZE) + Mesh.COLOR_INDEX] = pColor;	
			 
			 i = i+3;
			 x = x+pSegmentWidth;
		 }
		 
		final VertexBufferObjectManager VBOM = getVertexBufferObjectManager();
		final HighPerformanceMeshVertexBufferObject pMeshVBO = new HighPerformanceMeshVertexBufferObject(VBOM, pBufferData, pBufferData.length, DrawType.DYNAMIC, true, Mesh.VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT);
		
//		pMesh = new Mesh(0, 0,pVertexCount,DrawMode.TRIANGLES,pMeshVBO){		
//			
//			float progress_x = 0;
//			
//			@Override
//		    protected void onManagedUpdate(final float pSecondsElapsed) {  
//				super.onManagedUpdate(pSecondsElapsed);
//				drawBySine(pSecondsElapsed);
//		        this.mMeshVertexBufferObject.setDirtyOnHardware(); // include this line
//		        progress_x+=(pSpeed*pSecondsElapsed);
//		    };
//			
//			void drawBySine(final float pSecondsElapsed){
//				final float[] pBuff = pMeshVBO.getBufferData();
//				for (int i = 0;i<((pTrianglesCount)*3);i++){ //FIRST part of triangles 
//					if (i%3==0||i==0||((i-1)%3==0)){
//						//every vertex (v0) of triangle must be connected to previous triangle at second vertex (v1) to prevent stairs
//						if (i%3==0&&i>0){ 
//							mHeightOffsetCurrent[i] = mHeightOffsetCurrent[i-2];
//						} else { 
//							mHeightOffsetCurrent[i] = getNormalizedSine(i+progress_x,  mTouchY, pTrianglesCount*3);
//						}
//					}  
//					pBuff[(i * Mesh.VERTEX_SIZE) + Mesh.VERTEX_INDEX_Y] = mHeightOffsetCurrent[i];
//				}
//			}
//			
//			float getNormalizedSine(float x, float halfY, float maxX) {	
//			    double factor = (2 * Math.PI) / maxX;
//			    return (float) ((Math.sin(x * factor) * halfY) + halfY);
//			}
//			
//		};
		return pMesh;
	}
	
}














