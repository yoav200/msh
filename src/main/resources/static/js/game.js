(function($) {
	"use strict"; // Start of use strict

	console.log("Game script Ready");

	// ~ private properties ===========================================================================================
	
	var dollyConfig = {
		cssClass : "dolly",
		offset	:   [-30, -65],
		step : 43
	}
	
	var nextPosition = [0, 0];

	var stepsCounter = 1;
	
	var config = {
		container : $("#roadContainer"),
		maxSteps : 200,
		steps : 20,
		stepWidth : 20,
		stepHeight : 12,
		marginX : 3,
		marginY : 3,
		decline : 8
	}

	var containerWidth = $(config.container).width();
	
	// ~ draw steps functions  ========================================================================================
	
	function createStep(position) {
		
		var step = $('<div data-toggle="popover" title="Step #' + stepsCounter + ' price $1" data-content="And here some amazing content. It is very engaging. Right?" />', {
			
		}).width(config.stepWidth)
			.height(config.stepHeight)
			.attr("id",'step-' + stepsCounter)
			.addClass("step")
			.click(function(e) {
				e.preventDefault();
				console.log(this.id)
			});
		
		$(step).css({
	        top: nextPosition[1]  + "px",
	        left: nextPosition[0] + "px"
	    });
		
		var xpos, ypos;
		
		switch(position) {
			case 'right': 
				xpos = ( nextPosition[0] + config.stepWidth + config.marginX);
				ypos = nextPosition[1] + calculateDecline(); 
				break;
			case 'left': 
				xpos = (nextPosition[0] - config.stepWidth - config.marginX);
				ypos = nextPosition[1] + calculateDecline();
				break;
			case 'under': 
				xpos = nextPosition[0];
				ypos = (nextPosition[1] + config.stepHeight + config.marginY);
				break;
		}
		
		nextPosition = [xpos, ypos];
		
		// very important, step counter
		stepsCounter++;
		
		// add popover for steps
		$(step).popover({
			trigger : 'hover',
			placement : 'auto top'
		});
		
		return step
	}

	function calculateDecline() {
		var stepsInLine = containerWidth / (config.stepWidth + config.marginX);
		return config.decline / stepsInLine;
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
					
					var step3 = createStep('under');
					config.container.append(step3);
					
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
					
					var step3 = createStep('under');
					config.container.append(step3);
					
					done = true;
				}
			}
			
			config.container.height(nextPosition[1] + 50)
		}
		
	}
	
	// ~ Draw dolly functions =========================================================================================
	
	function drawDolly(step) {
		var dolly = $('<div/>', {
			"id" : 'dolly',
			"class" : dollyConfig.cssClass,
			"click" : function(e) {
				e.preventDefault();
				jumpToNextStep()
			}
		});
		
		var pos = $(step).position();
		
		$(dolly).css({
	        top: pos.top + dollyConfig.offset[1],
	        left: step.position().left + dollyConfig.offset[0]
	    });
		
		config.container.append(dolly);
	}
	
	function jumpToNextStep() {
		console.log("Jumping")
		
		var currentStep = $("#step-" + dollyConfig.step).position();
		dollyConfig.step++;
		var nextStep = $("#step-" + dollyConfig.step).position();
		
		var moveY = ("+=" + (nextStep.top - currentStep.top));
		var moveX = ("-=" + (currentStep.left - nextStep.left))
		
		$("#dolly").animate({ "left" : moveX, "top" : moveY}, "slow" );
		
		
		if(nextStep.left >= currentStep.left) { // moving right
			
			$("#dolly").removeClass("dollyFlip")
		} else { // moving left
			$("#dolly").addClass("dollyFlip")
		}
		
	}
	
	// ~ APIs =========================================================================================================
	
	function drawSteps() {
		for (var i = 0; i < config.steps; i++) {
			drawSinglePart()
		}
		
		setTimeout(function() {
			var currentStep = $("#step-" + dollyConfig.step);
		    drawDolly(currentStep);
		}, 1000);	 
	}
	
	drawSteps();
	
	
	// show hide scroll to top arrow
	$(window).scroll(function() {
		
		// infinite scroll
		if ($(window).scrollTop() == $(document).height() - $(window).height()) {
			console.log("adding road");
			drawSinglePart()
		}
		
	});
	
})(jQuery); // End of use strict
