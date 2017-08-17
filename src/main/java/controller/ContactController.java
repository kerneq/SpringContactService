package controller;

import entity.Contact;
import interfaces.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import util.Message;
import util.UrlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;


/**
 * Created by iters on 8/15/17.
 */

@RequestMapping("/contacts")
@Controller
public class ContactController {
    private final Logger logger = LoggerFactory.getLogger(ContactController.class);
    private ContactService contactService;
    private MessageSource messageSource;

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model uiModel) {
        logger.info("Listing contacts");
        List<Contact> contacts = contactService.findAll();
        uiModel.addAttribute("contacts", contacts);
        logger.info("№ of contacts is " + contacts.size());
        return "contacts/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model uiMidel) {
        Contact contact = contactService.findById(id);
        uiMidel.addAttribute("contact", contact);
        return "contacts/show";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = "form")
    public String update(@Valid Contact contact, BindingResult bindingResult,
                         Model uiModel, HttpServletRequest request,
                         RedirectAttributes redirectAttributes,
                         Locale locale) {
        logger.info("updating contact");
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("message", new Message("error",
                    messageSource.getMessage("contact_save_fault",
                            new Object[] {}, locale)));
            uiModel.addAttribute("contact", contact);
            return "contacts/update";
        }

        uiModel.asMap().clear();
        redirectAttributes.addFlashAttribute("message",
                new Message("success",
                        messageSource.getMessage("contact_save_success",
                                new Object[] {}, locale)));
        contactService.save(contact);
        return "redirect:/contacts/" + UrlUtil.encodeUrlPathSegment(
                contact.getId().toString(), request);
    }

    @RequestMapping(value = "/{id}", params="form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("contact", contactService.findById(id));
        return "contacts/update";
    }

    @RequestMapping(params = "form", method = RequestMethod.POST)
    public String create(@Valid Contact contact,
                         BindingResult bindingResult,
                         Model uiModel,
                         HttpServletRequest httpServletRequest,
                         RedirectAttributes redirectAttributes,
                         Locale locale) {
        logger.info("creating contact");
        if(bindingResult.hasErrors()) {
            uiModel.addAttribute("message",
                    new Message("error",
                            messageSource.getMessage("contact_save_fail",
                            new Object[] {}, locale)));
            uiModel.addAttribute("contact", contact);
            return "contacts/create";
        }

        uiModel.asMap().clear();
        redirectAttributes.addFlashAttribute("message",
                new Message("success",
                        messageSource.getMessage("contact_save_success",
                                new Object[] {}, locale)));
        logger.info("contact id " + contact.getId());
        contactService.save(contact);
        return "redirect:/contacts/" +
                UrlUtil.encodeUrlPathSegment(contact.getId() .toString(),
                        httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        Contact contact = new Contact();
        uiModel.addAttribute("contact", contact);
        return "contacts/create";
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setContactService(ContactService contactService) {
        this.contactService = contactService;
    }
}
