package resrov;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class Trapi {

	static {
		Native.register("TrapDoor.dll");
	}
	
	public static native int trapi_session_start();
	public static native int trapi_session_end(int sessionId);
	public static native String trapi_get(int sid, String url, String data);
	public static native String trapi_navigate(int sid, String url, String data);
	public static native Pointer trapi_screenshot(int sid);
	public static native int trapi_set_size(int sid, int w, int h);
	public static native void trapi_dll_cleanup();
	public static native int trapi_load_image(int sid, String path, String id);
	public static native int trapi_image_save(int sid, String path);
	public static native int trapi_type_key(int sessionId, char key);
	public static native String trapi_image_search(int sessionId, String bmpStr, int aVar, boolean repaint);
	public static native String trapi_image_search_area(int sessionId, String bmpStr, int aVar, boolean repaint, int top, int left, int bottom, int right);
	public static native NativeLong trapi_pixel_checksum_area(int sessionId, boolean repaint, int top, int left, int bottom, int right);
	public static native NativeLong trapi_pixel_color(int sessionId, int x, int y);
	public static native String trapi_pixel_search_area(int sessionId, int color, int aVar, boolean repaint, int top, int left, int bottom, int right);
	public static native String trapi_pop_source(int sessionId);
	public static native int trapi_mouse_move(int sessionId, int x, int y);
	public static native int trapi_mouse_down(int sessionId, int x, int y);
	public static native int trapi_mouse_up(int sessionId, int x, int y);
	
	public static void main(String[] args) {
		int sessionId = Trapi.trapi_session_start();
		String src = Trapi.trapi_get(sessionId, "http://www.google.de", "");
		System.out.println(src);
		Trapi.trapi_session_end(sessionId);
	}

}
