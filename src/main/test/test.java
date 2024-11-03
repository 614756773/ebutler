//import com.huoguo.model.bean.Comment;
//import com.huoguo.model.bean.Log;
//import com.huoguo.svc.dao.BookCatMapper;
//import com.huoguo.svc.dao.BookMapper;
//import com.huoguo.svc.dao.LogMapper;
//import com.huoguo.svc.dao.PunishMapper;
//import com.huoguo.core.service.BookCatService;
//import com.huoguo.core.service.BookService;
//import com.huoguo.core.service.BorrowService;
//import com.huoguo.core.service.CommentService;
//import com.huoguo.svc.shiro.ShiroUtil;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.Date;
//
//@ContextConfiguration(locations = { "classpath:spring.xml" })
//@RunWith(SpringJUnit4ClassRunner.class)
//public class test{
//    @Autowired
//    private BookCatService bookCatService;
//    @Autowired
//    private BookService bookService;
//    @Autowired
//    private BorrowService borrowService;
//    @Autowired
//    private PunishMapper punishMapper;
//    @Autowired
//    private BookCatService bookCatDao;
//    @Autowired
//    private LogMapper logMapper;
//    @Autowired
//    private CommentService commentService;
//    @Test
//    public  void a() {
////        System.out.println(dao.selectByPrimaryKey(1));
//        System.out.println(bookCatService.findLastId());
//        System.out.println(bookCatService.findByName("人文类"));
//    }
//
//    @Test
//    public void bookdao(){
//        System.out.println(bookService.findLastId());
//    }
//
//    @Test
//    public void borrow(){
//        System.out.println(borrowService.findListByRid(1).get(0).getBookid());
//    }
//    @Test
//    public void punish(){
//        Object o=punishMapper.selectLastId();
//        System.out.println(o);
//    }
//
//    @Test
//    public void shirotest(){
//        String jm=ShiroUtil.md5Hash("123456","614756773");
//        System.out.println(jm);
//        System.out.println(ShiroUtil.md5Hash("1","9956"));
//    }
//
//    @Test
//    public void logdao(){
//        Log log=new Log();
//        log.setOperresult("success");
//        log.setOperator("1");
//        log.setIp("0:0:0:0:0:0:0:1");
//        log.setMethod("com.huoguo.svc.controller.LoginController.loginHandler(String,String,HttpSession)");
//        log.setTime(new Date());
//        logMapper.insert(log);
//    }
//    @Test
//    public void commenttest(){
//        Comment comment=commentService.find(1,5);
//        comment.setComment("评论啊");
//        commentService.update(comment);
//    }
//
//}
