package com.mbrlabs.mundus.editor.profiling;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author JamesTKhan
 * @version June 30, 2022
 */
public class MundusGL20Interceptor extends MundusGLInterceptor {

    protected final GL20 gl20;

    protected MundusGL20Interceptor(GLProfiler profiler, GL20 gl20) {
        super(profiler);
        this.gl20 = gl20;
    }

    private void check () {
        int error = gl20.glGetError();
        while (error != GL20.GL_NO_ERROR) {
            glProfiler.getListener().onError(error);
            error = gl20.glGetError();
        }
    }

    @Override
    public void glActiveTexture (int texture) {
        incrementCalls();
        gl20.glActiveTexture(texture);
        check();
    }

    @Override
    public void glBindTexture (int target, int texture) {
        incrementTextureBindings();
        incrementCalls();
        gl20.glBindTexture(target, texture);
        check();
    }

    @Override
    public void glBlendFunc (int sfactor, int dfactor) {
        incrementCalls();
        gl20.glBlendFunc(sfactor, dfactor);
        check();
    }

    @Override
    public void glClear (int mask) {
        incrementCalls();
        gl20.glClear(mask);
        check();
    }

    @Override
    public void glClearColor (float red, float green, float blue, float alpha) {
        incrementCalls();
        gl20.glClearColor(red, green, blue, alpha);
        check();
    }

    @Override
    public void glClearDepthf (float depth) {
        incrementCalls();
        gl20.glClearDepthf(depth);
        check();
    }

    @Override
    public void glClearStencil (int s) {
        incrementCalls();
        gl20.glClearStencil(s);
        check();
    }

    @Override
    public void glColorMask (boolean red, boolean green, boolean blue, boolean alpha) {
        incrementCalls();
        gl20.glColorMask(red, green, blue, alpha);
        check();
    }

    @Override
    public void glCompressedTexImage2D (int target, int level, int internalformat, int width, int height, int border,
                                        int imageSize, Buffer data) {
        incrementCalls();
        gl20.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
        check();
    }

    @Override
    public void glCompressedTexSubImage2D (int target, int level, int xoffset, int yoffset, int width, int height, int format,
                                           int imageSize, Buffer data) {
        incrementCalls();
        gl20.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
        check();
    }

    @Override
    public void glCopyTexImage2D (int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        incrementCalls();
        gl20.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
        check();
    }

    @Override
    public void glCopyTexSubImage2D (int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        incrementCalls();
        gl20.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
        check();
    }

    @Override
    public void glCullFace (int mode) {
        incrementCalls();
        gl20.glCullFace(mode);
        check();
    }

    @Override
    public void glDeleteTextures (int n, IntBuffer textures) {
        incrementCalls();
        gl20.glDeleteTextures(n, textures);
        check();
    }

    @Override
    public void glDeleteTexture (int texture) {
        incrementCalls();
        gl20.glDeleteTexture(texture);
        check();
    }

    @Override
    public void glDepthFunc (int func) {
        incrementCalls();
        gl20.glDepthFunc(func);
        check();
    }

    @Override
    public void glDepthMask (boolean flag) {
        incrementCalls();
        gl20.glDepthMask(flag);
        check();
    }

    @Override
    public void glDepthRangef (float zNear, float zFar) {
        incrementCalls();
        gl20.glDepthRangef(zNear, zFar);
        check();
    }

    @Override
    public void glDisable (int cap) {
        incrementCalls();
        gl20.glDisable(cap);
        check();
    }

    @Override
    public void glDrawArrays (int mode, int first, int count) {
        putVertexCount(count);
        incrementDrawCalls();
        incrementCalls();
        gl20.glDrawArrays(mode, first, count);
        check();
    }

    @Override
    public void glDrawElements (int mode, int count, int type, Buffer indices) {
        putVertexCount(count);
        incrementDrawCalls();
        incrementCalls();
        gl20.glDrawElements(mode, count, type, indices);
        check();
    }

    @Override
    public void glEnable (int cap) {
        incrementCalls();
        gl20.glEnable(cap);
        check();
    }

    @Override
    public void glFinish () {
        incrementCalls();
        gl20.glFinish();
        check();
    }

    @Override
    public void glFlush () {
        incrementCalls();
        gl20.glFlush();
        check();
    }

    @Override
    public void glFrontFace (int mode) {
        incrementCalls();
        gl20.glFrontFace(mode);
        check();
    }

    @Override
    public void glGenTextures (int n, IntBuffer textures) {
        incrementCalls();
        gl20.glGenTextures(n, textures);
        check();
    }

    @Override
    public int glGenTexture () {
        incrementCalls();
        int result = gl20.glGenTexture();
        check();
        return result;
    }

    @Override
    public int glGetError () {
        incrementCalls();
        //Errors by glGetError are undetectable
        return gl20.glGetError();
    }

    @Override
    public void glGetIntegerv (int pname, IntBuffer params) {
        incrementCalls();
        gl20.glGetIntegerv(pname, params);
        check();
    }

    @Override
    public String glGetString (int name) {
        incrementCalls();
        String result = gl20.glGetString(name);
        check();
        return result;
    }

    @Override
    public void glHint (int target, int mode) {
        incrementCalls();
        gl20.glHint(target, mode);
        check();
    }

    @Override
    public void glLineWidth (float width) {
        incrementCalls();
        gl20.glLineWidth(width);
        check();
    }

    @Override
    public void glPixelStorei (int pname, int param) {
        incrementCalls();
        gl20.glPixelStorei(pname, param);
        check();
    }

    @Override
    public void glPolygonOffset (float factor, float units) {
        incrementCalls();
        gl20.glPolygonOffset(factor, units);
        check();
    }

    @Override
    public void glReadPixels (int x, int y, int width, int height, int format, int type, Buffer pixels) {
        incrementCalls();
        gl20.glReadPixels(x, y, width, height, format, type, pixels);
        check();
    }

    @Override
    public void glScissor (int x, int y, int width, int height) {
        incrementCalls();
        gl20.glScissor(x, y, width, height);
        check();
    }

    @Override
    public void glStencilFunc (int func, int ref, int mask) {
        incrementCalls();
        gl20.glStencilFunc(func, ref, mask);
        check();
    }

    @Override
    public void glStencilMask (int mask) {
        incrementCalls();
        gl20.glStencilMask(mask);
        check();
    }

    @Override
    public void glStencilOp (int fail, int zfail, int zpass) {
        incrementCalls();
        gl20.glStencilOp(fail, zfail, zpass);
        check();
    }

    @Override
    public void glTexImage2D (int target, int level, int internalformat, int width, int height, int border, int format, int type,
                              Buffer pixels) {
        incrementCalls();
        gl20.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        check();
    }

    @Override
    public void glTexParameterf (int target, int pname, float param) {
        incrementCalls();
        gl20.glTexParameterf(target, pname, param);
        check();
    }

    @Override
    public void glTexSubImage2D (int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
                                 Buffer pixels) {
        incrementCalls();
        gl20.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
        check();
    }

    @Override
    public void glViewport (int x, int y, int width, int height) {
        incrementCalls();
        gl20.glViewport(x, y, width, height);
        check();
    }

    @Override
    public void glAttachShader (int program, int shader) {
        incrementCalls();
        gl20.glAttachShader(program, shader);
        check();
    }

    @Override
    public void glBindAttribLocation (int program, int index, String name) {
        incrementCalls();
        gl20.glBindAttribLocation(program, index, name);
        check();
    }

    @Override
    public void glBindBuffer (int target, int buffer) {
        incrementCalls();
        gl20.glBindBuffer(target, buffer);
        check();
    }

    @Override
    public void glBindFramebuffer (int target, int framebuffer) {
        incrementCalls();
        gl20.glBindFramebuffer(target, framebuffer);
        check();
    }

    @Override
    public void glBindRenderbuffer (int target, int renderbuffer) {
        incrementCalls();
        gl20.glBindRenderbuffer(target, renderbuffer);
        check();
    }

    @Override
    public void glBlendColor (float red, float green, float blue, float alpha) {
        incrementCalls();
        gl20.glBlendColor(red, green, blue, alpha);
        check();
    }

    @Override
    public void glBlendEquation (int mode) {
        incrementCalls();
        gl20.glBlendEquation(mode);
        check();
    }

    @Override
    public void glBlendEquationSeparate (int modeRGB, int modeAlpha) {
        incrementCalls();
        gl20.glBlendEquationSeparate(modeRGB, modeAlpha);
        check();
    }

    @Override
    public void glBlendFuncSeparate (int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        incrementCalls();
        gl20.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        check();
    }

    @Override
    public void glBufferData (int target, int size, Buffer data, int usage) {
        incrementCalls();
        gl20.glBufferData(target, size, data, usage);
        check();
    }

    @Override
    public void glBufferSubData (int target, int offset, int size, Buffer data) {
        incrementCalls();
        gl20.glBufferSubData(target, offset, size, data);
        check();
    }

    @Override
    public int glCheckFramebufferStatus (int target) {
        incrementCalls();
        int result = gl20.glCheckFramebufferStatus(target);
        check();
        return result;
    }

    @Override
    public void glCompileShader (int shader) {
        incrementCalls();
        gl20.glCompileShader(shader);
        check();
    }

    @Override
    public int glCreateProgram () {
        incrementCalls();
        int result = gl20.glCreateProgram();
        check();
        return result;
    }

    @Override
    public int glCreateShader (int type) {
        incrementCalls();
        int result = gl20.glCreateShader(type);
        check();
        return result;
    }

    @Override
    public void glDeleteBuffer (int buffer) {
        incrementCalls();
        gl20.glDeleteBuffer(buffer);
        check();
    }

    @Override
    public void glDeleteBuffers (int n, IntBuffer buffers) {
        incrementCalls();
        gl20.glDeleteBuffers(n, buffers);
        check();
    }

    @Override
    public void glDeleteFramebuffer (int framebuffer) {
        incrementCalls();
        gl20.glDeleteFramebuffer(framebuffer);
        check();
    }

    @Override
    public void glDeleteFramebuffers (int n, IntBuffer framebuffers) {
        incrementCalls();
        gl20.glDeleteFramebuffers(n, framebuffers);
        check();
    }

    @Override
    public void glDeleteProgram (int program) {
        incrementCalls();
        gl20.glDeleteProgram(program);
        check();
    }

    @Override
    public void glDeleteRenderbuffer (int renderbuffer) {
        incrementCalls();
        gl20.glDeleteRenderbuffer(renderbuffer);
        check();
    }

    @Override
    public void glDeleteRenderbuffers (int n, IntBuffer renderbuffers) {
        incrementCalls();
        gl20.glDeleteRenderbuffers(n, renderbuffers);
        check();
    }

    @Override
    public void glDeleteShader (int shader) {
        incrementCalls();
        gl20.glDeleteShader(shader);
        check();
    }

    @Override
    public void glDetachShader (int program, int shader) {
        incrementCalls();
        gl20.glDetachShader(program, shader);
        check();
    }

    @Override
    public void glDisableVertexAttribArray (int index) {
        incrementCalls();
        gl20.glDisableVertexAttribArray(index);
        check();
    }

    @Override
    public void glDrawElements (int mode, int count, int type, int indices) {
        putVertexCount(count);
        incrementDrawCalls();
        incrementCalls();
        gl20.glDrawElements(mode, count, type, indices);
        check();
    }

    @Override
    public void glEnableVertexAttribArray (int index) {
        incrementCalls();
        gl20.glEnableVertexAttribArray(index);
        check();
    }

    @Override
    public void glFramebufferRenderbuffer (int target, int attachment, int renderbuffertarget, int renderbuffer) {
        incrementCalls();
        gl20.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
        check();
    }

    @Override
    public void glFramebufferTexture2D (int target, int attachment, int textarget, int texture, int level) {
        incrementCalls();
        gl20.glFramebufferTexture2D(target, attachment, textarget, texture, level);
        check();
    }

    @Override
    public int glGenBuffer () {
        incrementCalls();
        int result = gl20.glGenBuffer();
        check();
        return result;
    }

    @Override
    public void glGenBuffers (int n, IntBuffer buffers) {
        incrementCalls();
        gl20.glGenBuffers(n, buffers);
        check();
    }

    @Override
    public void glGenerateMipmap (int target) {
        incrementCalls();
        gl20.glGenerateMipmap(target);
        check();
    }

    @Override
    public int glGenFramebuffer () {
        incrementCalls();
        int result = gl20.glGenFramebuffer();
        check();
        return result;
    }

    @Override
    public void glGenFramebuffers (int n, IntBuffer framebuffers) {
        incrementCalls();
        gl20.glGenFramebuffers(n, framebuffers);
        check();
    }

    @Override
    public int glGenRenderbuffer () {
        incrementCalls();
        int result = gl20.glGenRenderbuffer();
        check();
        return result;
    }

    @Override
    public void glGenRenderbuffers (int n, IntBuffer renderbuffers) {
        incrementCalls();
        gl20.glGenRenderbuffers(n, renderbuffers);
        check();
    }

    @Override
    public String glGetActiveAttrib (int program, int index, IntBuffer size, IntBuffer type) {
        incrementCalls();
        String result = gl20.glGetActiveAttrib(program, index, size, type);
        check();
        return result;
    }

    @Override
    public String glGetActiveUniform (int program, int index, IntBuffer size, IntBuffer type) {
        incrementCalls();
        String result = gl20.glGetActiveUniform(program, index, size, type);
        check();
        return result;
    }

    @Override
    public void glGetAttachedShaders (int program, int maxcount, Buffer count, IntBuffer shaders) {
        incrementCalls();
        gl20.glGetAttachedShaders(program, maxcount, count, shaders);
        check();
    }

    @Override
    public int glGetAttribLocation (int program, String name) {
        incrementCalls();
        int result = gl20.glGetAttribLocation(program, name);
        check();
        return result;
    }

    @Override
    public void glGetBooleanv (int pname, Buffer params) {
        incrementCalls();
        gl20.glGetBooleanv(pname, params);
        check();
    }

    @Override
    public void glGetBufferParameteriv (int target, int pname, IntBuffer params) {
        incrementCalls();
        gl20.glGetBufferParameteriv(target, pname, params);
        check();
    }

    @Override
    public void glGetFloatv (int pname, FloatBuffer params) {
        incrementCalls();
        gl20.glGetFloatv(pname, params);
        check();
    }

    @Override
    public void glGetFramebufferAttachmentParameteriv (int target, int attachment, int pname, IntBuffer params) {
        incrementCalls();
        gl20.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
        check();
    }

    @Override
    public void glGetProgramiv (int program, int pname, IntBuffer params) {
        incrementCalls();
        gl20.glGetProgramiv(program, pname, params);
        check();
    }

    @Override
    public String glGetProgramInfoLog (int program) {
        incrementCalls();
        String result = gl20.glGetProgramInfoLog(program);
        check();
        return result;
    }

    @Override
    public void glGetRenderbufferParameteriv (int target, int pname, IntBuffer params) {
        incrementCalls();
        gl20.glGetRenderbufferParameteriv(target, pname, params);
        check();
    }

    @Override
    public void glGetShaderiv (int shader, int pname, IntBuffer params) {
        incrementCalls();
        gl20.glGetShaderiv(shader, pname, params);
        check();
    }

    @Override
    public String glGetShaderInfoLog (int shader) {
        incrementCalls();
        String result = gl20.glGetShaderInfoLog(shader);
        check();
        return result;
    }

    @Override
    public void glGetShaderPrecisionFormat (int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        incrementCalls();
        gl20.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
        check();
    }

    @Override
    public void glGetTexParameterfv (int target, int pname, FloatBuffer params) {
        incrementCalls();
        gl20.glGetTexParameterfv(target, pname, params);
        check();
    }

    @Override
    public void glGetTexParameteriv (int target, int pname, IntBuffer params) {
        incrementCalls();
        gl20.glGetTexParameteriv(target, pname, params);
        check();
    }

    @Override
    public void glGetUniformfv (int program, int location, FloatBuffer params) {
        incrementCalls();
        gl20.glGetUniformfv(program, location, params);
        check();
    }

    @Override
    public void glGetUniformiv (int program, int location, IntBuffer params) {
        incrementCalls();
        gl20.glGetUniformiv(program, location, params);
        check();
    }

    @Override
    public int glGetUniformLocation (int program, String name) {
        incrementCalls();
        int result = gl20.glGetUniformLocation(program, name);
        check();
        return result;
    }

    @Override
    public void glGetVertexAttribfv (int index, int pname, FloatBuffer params) {
        incrementCalls();
        gl20.glGetVertexAttribfv(index, pname, params);
        check();
    }

    @Override
    public void glGetVertexAttribiv (int index, int pname, IntBuffer params) {
        incrementCalls();
        gl20.glGetVertexAttribiv(index, pname, params);
        check();
    }

    @Override
    public void glGetVertexAttribPointerv (int index, int pname, Buffer pointer) {
        incrementCalls();
        gl20.glGetVertexAttribPointerv(index, pname, pointer);
        check();
    }

    @Override
    public boolean glIsBuffer (int buffer) {
        incrementCalls();
        boolean result = gl20.glIsBuffer(buffer);
        check();
        return result;
    }

    @Override
    public boolean glIsEnabled (int cap) {
        incrementCalls();
        boolean result = gl20.glIsEnabled(cap);
        check();
        return result;
    }

    @Override
    public boolean glIsFramebuffer (int framebuffer) {
        incrementCalls();
        boolean result = gl20.glIsFramebuffer(framebuffer);
        check();
        return result;
    }

    @Override
    public boolean glIsProgram (int program) {
        incrementCalls();
        boolean result = gl20.glIsProgram(program);
        check();
        return result;
    }

    @Override
    public boolean glIsRenderbuffer (int renderbuffer) {
        incrementCalls();
        boolean result = gl20.glIsRenderbuffer(renderbuffer);
        check();
        return result;
    }

    @Override
    public boolean glIsShader (int shader) {
        incrementCalls();
        boolean result = gl20.glIsShader(shader);
        check();
        return result;
    }

    @Override
    public boolean glIsTexture (int texture) {
        incrementCalls();
        boolean result = gl20.glIsTexture(texture);
        check();
        return result;
    }

    @Override
    public void glLinkProgram (int program) {
        incrementCalls();
        gl20.glLinkProgram(program);
        check();
    }

    @Override
    public void glReleaseShaderCompiler () {
        incrementCalls();
        gl20.glReleaseShaderCompiler();
        check();
    }

    @Override
    public void glRenderbufferStorage (int target, int internalformat, int width, int height) {
        incrementCalls();
        gl20.glRenderbufferStorage(target, internalformat, width, height);
        check();
    }

    @Override
    public void glSampleCoverage (float value, boolean invert) {
        incrementCalls();
        gl20.glSampleCoverage(value, invert);
        check();
    }

    @Override
    public void glShaderBinary (int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        incrementCalls();
        gl20.glShaderBinary(n, shaders, binaryformat, binary, length);
        check();
    }

    @Override
    public void glShaderSource (int shader, String string) {
        incrementCalls();
        gl20.glShaderSource(shader, string);
        check();
    }

    @Override
    public void glStencilFuncSeparate (int face, int func, int ref, int mask) {
        incrementCalls();
        gl20.glStencilFuncSeparate(face, func, ref, mask);
        check();
    }

    @Override
    public void glStencilMaskSeparate (int face, int mask) {
        incrementCalls();
        gl20.glStencilMaskSeparate(face, mask);
        check();
    }

    @Override
    public void glStencilOpSeparate (int face, int fail, int zfail, int zpass) {
        incrementCalls();
        gl20.glStencilOpSeparate(face, fail, zfail, zpass);
        check();
    }

    @Override
    public void glTexParameterfv (int target, int pname, FloatBuffer params) {
        incrementCalls();
        gl20.glTexParameterfv(target, pname, params);
        check();
    }

    @Override
    public void glTexParameteri (int target, int pname, int param) {
        incrementCalls();
        gl20.glTexParameteri(target, pname, param);
        check();
    }

    @Override
    public void glTexParameteriv (int target, int pname, IntBuffer params) {
        incrementCalls();
        gl20.glTexParameteriv(target, pname, params);
        check();
    }

    @Override
    public void glUniform1f (int location, float x) {
        incrementCalls();
        gl20.glUniform1f(location, x);
        check();
    }

    @Override
    public void glUniform1fv (int location, int count, FloatBuffer v) {
        incrementCalls();
        gl20.glUniform1fv(location, count, v);
        check();
    }

    @Override
    public void glUniform1fv (int location, int count, float[] v, int offset) {
        incrementCalls();
        gl20.glUniform1fv(location, count, v, offset);
        check();
    }

    @Override
    public void glUniform1i (int location, int x) {
        incrementCalls();
        gl20.glUniform1i(location, x);
        check();
    }

    @Override
    public void glUniform1iv (int location, int count, IntBuffer v) {
        incrementCalls();
        gl20.glUniform1iv(location, count, v);
        check();
    }

    @Override
    public void glUniform1iv (int location, int count, int[] v, int offset) {
        incrementCalls();
        gl20.glUniform1iv(location, count, v, offset);
        check();
    }

    @Override
    public void glUniform2f (int location, float x, float y) {
        incrementCalls();
        gl20.glUniform2f(location, x, y);
        check();
    }

    @Override
    public void glUniform2fv (int location, int count, FloatBuffer v) {
        incrementCalls();
        gl20.glUniform2fv(location, count, v);
        check();
    }

    @Override
    public void glUniform2fv (int location, int count, float[] v, int offset) {
        incrementCalls();
        gl20.glUniform2fv(location, count, v, offset);
        check();
    }

    @Override
    public void glUniform2i (int location, int x, int y) {
        incrementCalls();
        gl20.glUniform2i(location, x, y);
        check();
    }

    @Override
    public void glUniform2iv (int location, int count, IntBuffer v) {
        incrementCalls();
        gl20.glUniform2iv(location, count, v);
        check();
    }

    @Override
    public void glUniform2iv (int location, int count, int[] v, int offset) {
        incrementCalls();
        gl20.glUniform2iv(location, count, v, offset);
        check();
    }

    @Override
    public void glUniform3f (int location, float x, float y, float z) {
        incrementCalls();
        gl20.glUniform3f(location, x, y, z);
        check();
    }

    @Override
    public void glUniform3fv (int location, int count, FloatBuffer v) {
        incrementCalls();
        gl20.glUniform3fv(location, count, v);
        check();
    }

    @Override
    public void glUniform3fv (int location, int count, float[] v, int offset) {
        incrementCalls();
        gl20.glUniform3fv(location, count, v, offset);
        check();
    }

    @Override
    public void glUniform3i (int location, int x, int y, int z) {
        incrementCalls();
        gl20.glUniform3i(location, x, y, z);
        check();
    }

    @Override
    public void glUniform3iv (int location, int count, IntBuffer v) {
        incrementCalls();
        gl20.glUniform3iv(location, count, v);
        check();
    }

    @Override
    public void glUniform3iv (int location, int count, int[] v, int offset) {
        incrementCalls();
        gl20.glUniform3iv(location, count, v, offset);
        check();
    }

    @Override
    public void glUniform4f (int location, float x, float y, float z, float w) {
        incrementCalls();
        gl20.glUniform4f(location, x, y, z, w);
        check();
    }

    @Override
    public void glUniform4fv (int location, int count, FloatBuffer v) {
        incrementCalls();
        gl20.glUniform4fv(location, count, v);
        check();
    }

    @Override
    public void glUniform4fv (int location, int count, float[] v, int offset) {
        incrementCalls();
        gl20.glUniform4fv(location, count, v, offset);
        check();
    }

    @Override
    public void glUniform4i (int location, int x, int y, int z, int w) {
        incrementCalls();
        gl20.glUniform4i(location, x, y, z, w);
        check();
    }

    @Override
    public void glUniform4iv (int location, int count, IntBuffer v) {
        incrementCalls();
        gl20.glUniform4iv(location, count, v);
        check();
    }

    @Override
    public void glUniform4iv (int location, int count, int[] v, int offset) {
        incrementCalls();
        gl20.glUniform4iv(location, count, v, offset);
        check();
    }

    @Override
    public void glUniformMatrix2fv (int location, int count, boolean transpose, FloatBuffer value) {
        incrementCalls();
        gl20.glUniformMatrix2fv(location, count, transpose, value);
        check();
    }

    @Override
    public void glUniformMatrix2fv (int location, int count, boolean transpose, float[] value, int offset) {
        incrementCalls();
        gl20.glUniformMatrix2fv(location, count, transpose, value, offset);
        check();
    }

    @Override
    public void glUniformMatrix3fv (int location, int count, boolean transpose, FloatBuffer value) {
        incrementCalls();
        gl20.glUniformMatrix3fv(location, count, transpose, value);
        check();
    }

    @Override
    public void glUniformMatrix3fv (int location, int count, boolean transpose, float[] value, int offset) {
        incrementCalls();
        gl20.glUniformMatrix3fv(location, count, transpose, value, offset);
        check();
    }

    @Override
    public void glUniformMatrix4fv (int location, int count, boolean transpose, FloatBuffer value) {
        incrementCalls();
        gl20.glUniformMatrix4fv(location, count, transpose, value);
        check();
    }

    @Override
    public void glUniformMatrix4fv (int location, int count, boolean transpose, float[] value, int offset) {
        incrementCalls();
        gl20.glUniformMatrix4fv(location, count, transpose, value, offset);
        check();
    }

    @Override
    public void glUseProgram (int program) {
        incrementShaderSwitches();
        incrementCalls();
        gl20.glUseProgram(program);
        check();
    }

    @Override
    public void glValidateProgram (int program) {
        incrementCalls();
        gl20.glValidateProgram(program);
        check();
    }

    @Override
    public void glVertexAttrib1f (int indx, float x) {
        incrementCalls();
        gl20.glVertexAttrib1f(indx, x);
        check();
    }

    @Override
    public void glVertexAttrib1fv (int indx, FloatBuffer values) {
        incrementCalls();
        gl20.glVertexAttrib1fv(indx, values);
        check();
    }

    @Override
    public void glVertexAttrib2f (int indx, float x, float y) {
        incrementCalls();
        gl20.glVertexAttrib2f(indx, x, y);
        check();
    }

    @Override
    public void glVertexAttrib2fv (int indx, FloatBuffer values) {
        incrementCalls();
        gl20.glVertexAttrib2fv(indx, values);
        check();
    }

    @Override
    public void glVertexAttrib3f (int indx, float x, float y, float z) {
        incrementCalls();
        gl20.glVertexAttrib3f(indx, x, y, z);
        check();
    }

    @Override
    public void glVertexAttrib3fv (int indx, FloatBuffer values) {
        incrementCalls();
        gl20.glVertexAttrib3fv(indx, values);
        check();
    }

    @Override
    public void glVertexAttrib4f (int indx, float x, float y, float z, float w) {
        incrementCalls();
        gl20.glVertexAttrib4f(indx, x, y, z, w);
        check();
    }

    @Override
    public void glVertexAttrib4fv (int indx, FloatBuffer values) {
        incrementCalls();
        gl20.glVertexAttrib4fv(indx, values);
        check();
    }

    @Override
    public void glVertexAttribPointer (int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        incrementCalls();
        gl20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        check();
    }

    @Override
    public void glVertexAttribPointer (int indx, int size, int type, boolean normalized, int stride, int ptr) {
        incrementCalls();
        gl20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
        check();
    }
}
