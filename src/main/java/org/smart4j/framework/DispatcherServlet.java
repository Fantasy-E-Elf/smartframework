package org.smart4j.framework;

import org.smart4j.framework.bean.Data;
import org.smart4j.framework.helper.*;
import org.smart4j.framework.util.JsonUtil;
import org.smart4j.framework.util.ReflectionUtil;
import org.smart4j.framework.util.StringUtil;
import org.smart4j.framework.bean.Handler;
import org.smart4j.framework.bean.Param;
import org.smart4j.framework.bean.View;
import top.smart4j.framework.helper.*;
import top.smart4j.framework.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * 请求转发器
 *
 * @author wang yi zhe
 * @since 1.0.0
 */

/**
 * 请求转发器
 */
@WebServlet(urlPatterns = "/*" ,loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化相关 Helper 类
        HelpLoader.init();
        //获取 ServletContext 对象（用去注册Servlet）
        ServletContext servletContext = getServletConfig().getServletContext();
        //注册处理JSP的Servlet
        ServletRegistration JspServlet = servletContext.getServletRegistration("jsp");
        JspServlet.addMapping(ConfigHelper.getAppJspPath() +"*");
        //注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath()+"*");

        UploadHelper.init(servletContext);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ServletHelper.init(request,response);
        try {

            //获取请求方法与请求路径
            String requestMethod = request.getMethod().toLowerCase();
            String requestPath = request.getPathInfo();

            if (requestPath.equals("/favicon.ico")){
                return;
            }
            //获取Action处理器
            Handler handler = ControllerHelper.getHandler(requestMethod,requestPath);
            if (handler != null){
                // 获取Controller 及 Bean 实例
                Class<?> controllerClss = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClss);
                Param param;
                //请求参数对象
                if (UploadHelper.isMultipart(request)){
                    param = UploadHelper.createParam(request);
                }else {
                    param = RequestHelper.createParam(request);
                }
                //调用Action方法
                Method actionMethod = handler.getActionMethod();
                Object result;  // = ReflectionUtil.invokeMethod(controllerBean,actionMethod,param);
                                //Action优化方案(if..else..给Action去参数)
                if (param.isEmpty()){
                    result = ReflectionUtil.invokeMethod(controllerBean,actionMethod);
                }else {
                    result = ReflectionUtil.invokeMethod(controllerBean,actionMethod,param);
                }
                // 处理Action方法返回值
                if(result instanceof View){
                    //返回JSP界面
                    handleViewResult((View) result, request,response);
                    }else if (result instanceof Data){
                        //返回 JSON 数据
                    handleDataResult((Data) result, request,response);
                }
            }
        }finally {
            ServletHelper.destroy();
        }
    }



    private void handleViewResult(View view,HttpServletRequest request , HttpServletResponse response) throws IOException,ServletException{
        String path = view.getPath();
        if (StringUtil.isNotEmpty(path)){
            if (path.startsWith("/")){
                response.sendRedirect(request.getContextPath()+path);
            }else {
                Map<String,Object> model = view.getModel();
                for (Map.Entry<String,Object> entry : model.entrySet()){
                    request.setAttribute(entry.getKey(),entry.getValue());
                }
                request.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(request,response);
            }
        }
    }
    private void handleDataResult(Data data,HttpServletRequest request , HttpServletResponse response) throws IOException,ServletException{
        Object model = data.getModel();
        if (model != null){
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }

}
