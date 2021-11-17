let app = Vue.createApp({
    template: 
            `<form name="ComposerMessage" id="ComposerMessage">

            <input type="radio" id="Magasin" name="destinataire" value="Magasin" checked>
              <label for="Magasin">Magasin</label>
      
              <input type="radio" id="Ecole" name="destinataire" value="Ecole" checked>
              <label for="Ecole">Ecole</label>
      
              <input type="radio" id="Entreprise" name="destinataire" value="Entreprise" checked>
              <label for="Entreprise">Entreprise</label>
      
              <select name="TypeMessage" id="TypeMessage" v-model="selected">
                
                <option>Demande Collaboration</option>
                <option>Réponse Générique</option>
      
              </select>
              <span>Sélectionné : {{ selected }}</span>
              <input type="button" name="Select" value="Type" @click="ajout(ComposerMessage)">
              <input type="button" @click="test">

              <add-field></add-field>
          </form>
          `,
    methods:{
        ajout : function (f){
            
            var container = document.getElementById("ComposerMessage");
            switch(this.selected){
                case "Réponse Générique":
                    alert("Réponse");
                    
                    var textarea = document.createElement("textarea");
                    textarea.name= "Message";
                    textarea.placeholder = "Message";
                    container.appendChild(textarea);
                    
                    break;
            }
        },
        test : function(){
            const option_empty = new Option("");
            var option_rg = new Option("Réponse Générique");
            const option_dc = new Option("Demande Collaboration");
            
            var container = document.getElementById("ComposerMessage");
            var select = document.createElement("select");
            select.name= "TypeMessage";
            select.add(option_empty);
            select.add(option_dc);
            select.add(option_rg);
            
            container.appendChild(select);
            
            var input = document.createElement("input");
            input.name = "Select";
            input.type = "button";
            input.value = "Type";
            input.onclick = "alert('tedt')";
            var field = document.createElement("");
            container.appendChild(field);
            
        }    
    },
    data: () => ({
        selected :''
    }),
})


app.component('add-field', {
    template:'<a th:href="${google.com}"><p>TEST</p></a>',
})

let vm = app.mount('#container')
