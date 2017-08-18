package controller;

import com.google.common.collect.Lists;
import entity.Contact;
import interfaces.ContactService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import util.ContactGrid;
import util.Message;
import util.UrlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
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
                         Locale locale,
                         @RequestParam(value = "file", required = false) Part file) {
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

        if (file != null) {
            logger.info("File name " + file.getName());
            logger.info("File size " + file.getSize());
            logger.info("File content type " + file.getContentType());
            byte[] fileContent = null;

            try {
                InputStream inputStream = file.getInputStream();
                if (inputStream == null) {
                    logger.info("File inputStream is null");
                }
                fileContent = IOUtils.toByteArray(inputStream);
                contact.setPhoto(fileContent);
            } catch (IOException ex) {
                logger.info("Error saving file");
            }
            contact.setPhoto(fileContent);
        }
        contact =  contactService.save(contact);

        return "redirect:/contacts/" +
                UrlUtil.encodeUrlPathSegment(contact.getId().toString(),
                        httpServletRequest);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        Contact contact = new Contact();
        uiModel.addAttribute("contact", contact);
        return "contacts/create";
    }

    @RequestMapping(value = "/listgrid", method = RequestMethod.GET,
    produces = "application/json")
    @ResponseBody
    public ContactGrid listGrid(
            @RequestParam( value = "page", required = false) Integer page,
            @RequestParam (value = "rows", required = false) Integer rows,
            @RequestParam (value = "sidx", required = false) String sortBy,
            @RequestParam (value = "sord", required = false) String order) {
        logger.info("Listing contacts for grid with page: {}, rows: {} ",
                page, rows);
        logger.info("Listing contacts for grid with sort: {}, order: {}",
                sortBy, order);
        Sort sort = null;
        String orderBy = sortBy;

        if (orderBy != null && orderBy.equalsIgnoreCase("birthDateString")) {
            orderBy = "birthDate";
        }

        if (orderBy != null && order != null) {
            if(order.equalsIgnoreCase("desc")) {
                sort = new Sort(Sort.Direction.DESC, orderBy);
            } else {
                sort = new Sort(Sort.Direction.ASC, orderBy);
            }
        }

        PageRequest pageRequest = null;
        if (sort != null) {
            pageRequest = new PageRequest(page - 1, rows, sort);
        } else {
            pageRequest = new PageRequest(page - 1, rows);
        }

        Page<Contact> contactPage = contactService.findAllByPage(pageRequest);
        ContactGrid contactGrid = new ContactGrid();
        contactGrid.setCurrentPage(contactPage.getNumber() + 1);
        contactGrid.setTotalPages(contactPage.getTotalPages());
        contactGrid.setTotalRecords((int) contactPage.getTotalElements());
        contactGrid.setContactData(Lists.newArrayList(contactPage.iterator()));
        return contactGrid;
    }

    @RequestMapping(value = "/photo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public byte[] downloadPhoto(@PathVariable("id") Long id) {
        Contact contact = contactService.findById(id);
        if (contact.getPhoto() != null) {
            logger.info("Download photo for id: {} with size {}",
                    contact.getId(), contact.getPhoto().length);
        }
        logger.warn("contact photo " + contact.getPhoto());
        return contact.getPhoto();
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
