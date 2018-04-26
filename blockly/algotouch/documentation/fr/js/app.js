$(document).ready(function () {

    var content = [
        { category: 'Accueil', title: 'Accueil', keys: 'Tous les mots en rapport non affichés', url:'#/home'},
        { category: 'Accueil', title: 'A propos', keys: '', url:'#/home'},
        { category: 'Manuel', title: 'Manuel utilisateur', keys: 'manuel utilisateur guide aide', url:'#/manual'},
        { category: 'A Propos', title: 'Informations utiles', keys: '', url:'#/about' },
        { category: 'Variables', title: 'Tableaux', keys: '', url:'#/arrays' },
        { category: 'Variables', title: 'Valeurs', keys: '', url:'#/values' },
        { category: 'Tableaux', title: 'Taille', keys: '', url:'#/length' },
        { category: 'Tableaux', title: 'Valeur de', keys: '', url:'#/getset' },
        { category: 'Tableaux', title: 'Mettre à', keys: '', url:'#/getset' },
        { category: 'Valeurs', title: 'Nombre', keys: '', url:'#/integer' },
        { category: 'Valeurs', title: 'Caractère', keys: '', url:'#/char' },
        { category: 'Valeurs', title: 'Lire message', keys: '', url:'#/read' },
        { category: 'Maths', title: 'Opérations', keys: '', url:'#/maths' },
        { category: 'Logique', title: 'Si alors sinon', keys: '', url:'#/if' },
        { category: 'Logique', title: 'Comparaison', keys: '', url:'#/logical' },
        { category: 'Macro', title: 'Nouvelle macro', keys: '', url:'#/new' },
        { category: 'Macro', title: 'Appel macro', keys: '', url:'#/call' },
       
        // etc
    ];

    $('.ui.search')
        .search({
            type: 'category',
    		source: content,
    		searchFields: ['category', 'title', 'keys'],
    			
        })
    ;

    $('.ui.styled.fluid.accordion')
        .accordion()
    ;

    $('ui.large.inverted.vertical.menu.top.attached')
        .tab()
    ;

});