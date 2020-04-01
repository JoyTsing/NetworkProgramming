package connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/*
HTTP/1.1 200 ok
 */
public class Response {

    private static final int BUFFER_SIZE = 1024;

    Request request;
    OutputStream output;

    public Response(OutputStream output) {
        this.output = output;
    }

    public Response(Request request) {
        this.request = request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException {
        File file = new File(ConnectorUtils.WEB_ROOT, request.getRequestURI());
        try {
            write(file, HttpStatus.SC_OK);
        } catch (IOException e) {
            write(new File(ConnectorUtils.WEB_ROOT, "404.html"), HttpStatus.SC_NOT_FOUND);
        }
    }

    private void write(File resource, HttpStatus status) throws IOException {
        try (FileInputStream fis = new FileInputStream(resource)) {
            output.write(ConnectorUtils.renderStatus(status).getBytes());
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = 0;
            while ((length = fis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                output.write(buffer, 0, length);
            }
        }


    }
}
