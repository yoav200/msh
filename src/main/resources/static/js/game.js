(function($) {
	"use strict"; // Start of use strict

	console.log("Game script Ready");

	var nextPosition = [0, 0];

	var stepsCounter = 1;
	
	var config = {
		container : $("#roadContainer"),
		maxSteps : 200,
		steps : 20,
		stepWidth : 12,
		stepHeight : 10,
		marginX : 3,
		marginY : 3
	}

	var containerWidth = $(config.container).width();
	
	function createStep(position) {
		//if(stepsCounter >= config.maxSteps) {
		//	return;
		//}
		
		var step = $('<div data-toggle="popover" title="Step #'+stepsCounter+' price $1" data-content="And here some amazing content. It is very engaging. Right?"/>', {
			"id" : 'step-' + stepsCounter,
			"class" : 'step',
			"click" : function(e) {
				e.preventDefault();
				console.log(this.id)
			}
		}).width(config.stepWidth)
			.height(config.stepHeight)
			.addClass("step");
		
		switch(position) {
			case 'right': setPositionToRight(step); break;
			case 'left': setPositionToLeft(step); break;
			case 'under': setPositionUnder(step); break;
		}
		
		// very important, step counter
		stepsCounter++;
		
		return step
	}

	
	function setPositionToRight(step) {
		$(step).css({
	        top: nextPosition[1]  + "px",
	        left: nextPosition[0] + "px"
	    });
		var xpos = ( nextPosition[0] + config.stepWidth + config.marginX);
		var ypos = nextPosition[1] + calculateDecline();
		nextPosition = [xpos, ypos];
	}
	
	function setPositionToLeft(step) {
		$(step).css({
	        top: nextPosition[1]  + "px",
	        left: nextPosition[0] + "px"
	    });
		var xpos = (nextPosition[0] - config.stepWidth - config.marginX);
		var ypos = nextPosition[1] + calculateDecline();;
		nextPosition = [xpos, ypos];
	}
	
	function setPositionUnder(step) {
		$(step).css({
	        top: nextPosition[1]  + "px",
	        left: nextPosition[0] + "px"
	    });
		
		var xpos = nextPosition[0];
		var ypos = (nextPosition[1] + config.stepHeight + config.marginY);
		nextPosition = [xpos, ypos];
	}
	
	function calculateDecline() {
		var declineBy = 5;
		var stepsInLine = containerWidth / (config.stepWidth + config.marginX);
		return declineBy / stepsInLine;
	}
	
	function drawSinglePart() {
		
		var direction = "right";
		var done = false;
		
		while(!done) {
			if(direction === "right") {
				if(nextPosition[0] + config.stepWidth < containerWidth) {
					var step = createStep('right');
					config.container.append(step);
				} else {
					var step1 = createStep('under');
					config.container.append(step1);
					
					var step2 = createStep('under');
					config.container.append(step2);
					direction = "left";
				}
			}
		
			if(direction === "left") {
				if(nextPosition[0] - config.stepWidth > 0) {
					var step = createStep('left');
					config.container.append(step);
				} else {
					var step1 = createStep('under');
					config.container.append(step1);
					
					var step2 = createStep('under');
					config.container.append(step2);
					
					done = true;
				}
			}
			
			config.container.height(nextPosition[1] + 50)
		}
		
	}
	
	function drawSteps() {
		
		for (var i = 0; i < config.steps; i++) {
			drawSinglePart()
		}
		
		
		// add popover for steps
		$('[data-toggle="popover"]').popover({
			trigger : 'hover',
			placement : 'auto top'
		});   
	}
	

	drawSteps();
	
})(jQuery); // End of use strict
