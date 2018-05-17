package intellif.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Zheng Xiaodong on 2017/6/13.
 */
@RestController
@RequestMapping("bank/sync")
public class BankSyncController {
    private static Logger LOG = LogManager.getLogger(BankImportController.class);

}
