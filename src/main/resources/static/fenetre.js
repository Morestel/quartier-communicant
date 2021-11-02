let app = Vue.createApp({

    data: () => ({
        idMsgPrecedent : -1,
        difference_x : 0,
        difference_y : 0,
        enCours : false
    }),
    methods: {
        afficher: function(){
            
            this.idMsgPrecedent = document.getElementById("dirty").innerHTML;
            if (document.getElementById("hiddenFenetre").style.display == "none"){
                document.getElementById("hiddenFenetre").style.display = "block";
            }
            else{
                document.getElementById("hiddenFenetre").style.display = "none";
            }
        },

        presser : function(event){
            this.origine_x = event.x - this.position_x;
            this.origine_y = event.y - this.position_y;
            var body = document.querySelector('body');
            var left = parseInt(getComputedStyle(body).getPropertyValue('--fenetre-x'), 10);
            var top = parseInt(getComputedStyle(body).getPropertyValue('--fenetre-y'), 10);
            this.difference_x = event.x - left;
            this.difference_y = event.y - top;
            this.enCours = true;
        },

        relacher : function(){
            this.enCours = false;
        },

        deplacer: function(event){
            
            var root = document.documentElement;
            if (this.enCours == true){
                let result_x = event.x - this.difference_x;
                let result_y = event.y - this.difference_y;
               
                root.style.setProperty("--fenetre-x", result_x + "px");
                root.style.setProperty("--fenetre-y", result_y + "px");
            }    
        }
        
    }

})

let vm = app.mount('#fenetre')