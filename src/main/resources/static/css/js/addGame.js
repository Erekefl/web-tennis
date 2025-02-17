function validatePlayer(inputId, datalistId,errorId) {
    let input = document.getElementById(inputId);
    let datalist = document.getElementById(inputId);
    let errorMessage = document.getElementById(errorId);

    let valid = false;
    for(let option of datalist.options){
    if(input.value === option.value){
    valid =true;
    break;
    }
    }
    if (!valid) {
    errorMessage.classList.remove("hidden");
    } else {
    errorMessage.classList.add("hidden");
    }
}

document.getElementById("player1").addEventListener("input",function(){
validatePlayer("player1","playersList1","player1Error");
});

document.getElementById("player2").addEventListener("input",function(){
validatePlayer("player2","playerList2","player2Error");
});
