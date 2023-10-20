@Slf4j
@Controller
public class GoodsController {


    @RequestMapping("/goods/test")
    @ResponseBody
    public String test() {
        return "hello word java!";
    }
}