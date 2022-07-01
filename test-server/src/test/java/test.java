import java.net.URLDecoder;

public class test {

    public static void main(String[] args) {
        String decode = URLDecoder.decode("dubbo%3A%2F%2F172.20.10.13%3A20880%2Forg.apache.dubbo.springboot.demo.DemoService%3Fanyhost%3Dtrue%26application%3Ddubbo-springboot-demo-provider%26background%3Dfalse%26deprecated%3Dfalse%26dubbo%3D2.0.2%26dynamic%3Dtrue%26generic%3Dfalse%26interface%3Dorg.apache.dubbo.springboot.demo.DemoService%26methods%3DsayHello%2CsayHelloAsync%26pid%3D11697%26release%3D3.0.10-SNAPSHOT%26service-name-mapping%3Dtrue%26side%3Dprovider%26timestamp%3D1656557077176\n" +
                "\n");
        System.out.println("decode = " + decode);
    }
}
