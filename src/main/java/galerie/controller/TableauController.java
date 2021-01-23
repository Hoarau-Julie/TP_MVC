package galerie.controller;

import galerie.dao.ArtisteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import galerie.dao.TableauRepository;
import galerie.entity.Tableau;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Edition des catégories, sans gestion des erreurs
 */
@Controller
@RequestMapping(path = "/tableau")
public class TableauController {

    @Autowired
    private TableauRepository dao;
    @Autowired
    private ArtisteRepository dao1;

    @GetMapping(path = "show")
    public String afficheTousLesTableaux(Model model) {
        model.addAttribute("tableaux", dao.findAll());
        return "afficheTableaux";
    }

    @GetMapping(path = "add")
    public String montreLeFormulairePourAjout(Model model) {
        model.addAttribute("tableau",new Tableau());
        model.addAttribute("artistes",dao1.findAll());
        return "formulaireTableau";
    }

    
    @PostMapping(path = "save")
    public String ajouteLeTableauPuisMontreLaListe(Tableau tableau, RedirectAttributes redirectInfo) {
        String message;
        try {
            // cf. https://www.baeldung.com/spring-data-crud-repository-save
            dao.save(tableau);
            // Le code de la catégorie a été initialisé par la BD au moment de l'insertion
            message = "Le tableau '" + tableau.getTitre() + "' a été correctement enregistré";
        } catch (DataIntegrityViolationException e) {
            // Les noms sont définis comme 'UNIQUE' 
            // En cas de doublon, JPA lève une exception de violation de contrainte d'intégrité
            message = "Erreur : Le tableau '" + tableau.getTitre() + "' existe déjà";
        }
        // RedirectAttributes permet de transmettre des informations lors d'une redirection,
        // Ici on transmet un message de succès ou d'erreur
        // Ce message est accessible et affiché dans la vue 'afficheTableau.html'
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show"; // POST-Redirect-GET : on se redirige vers l'affichage de la liste		
    }

    @GetMapping(path = "delete")
    public String supprimeUnTableau(@RequestParam("id") Tableau tableau, RedirectAttributes redirectInfo) {
        String message = "Le tableau '" + tableau.getTitre() + "' a bien été supprimé";
        try {
            dao.delete(tableau); // Ici on peut avoir une erreur (Si le tableau n'a pas été vendu par exemple)
        } catch (DataIntegrityViolationException e) {
            
            message = "Erreur : Impossible de supprimer le tableau '" + tableau.getTitre() + "', il faut d'abord vérifier que le tableau soit retiré de l'exposition";
        }
        // RedirectAttributes permet de transmettre des informations lors d'une redirection,
        // Ici on transmet un message de succès ou d'erreur
        // Ce message est accessible et affiché dans la vue 'afficheTableau.html'
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show"; // on se redirige vers l'affichage de la liste
    }
}
