(function($) {
	"use strict"; // Start of use strict

	console.log("Game 2 script Ready");

	// ~ private properties ===========================================================================================
	
	function StepData(id, x, y) {
		this.id = id;
	    this.x = x;
	    this.y = y;
	}
	
	var stepsCollection = {
	   size: 1, 
	   array: [new StepData(1,0,0)],
	   add: function(x, y) {
	       this.array.push(new StepData(++this.size, x, y));
	   },
	   first: function() {
		   return this.array[0];
	   },
	   last: function() {
		   return this.array[this.array.length-1];
	   }
	}

	var config = {
		container : $("#roadContainer"),
		maxSteps : 200,
		sections : 20,
		stepWidth : 20,
		stepHeight : 12,
		marginX : 3,
		marginY : 3,
		decline : 8,
		dolly : {
			cssClass : "dolly",
			offset	:   [-30, -65],
			step : 43
		}
	}

	var containerWidth = $(config.container).width();
	
	var decline = calculateDecline();
		
	function calculateDecline() {
		var stepsInLine = containerWidth / (config.stepWidth + config.marginX);
		return config.decline / stepsInLine;
	}
	// ~ draw steps functions  ========================================================================================
	
	
	
	function calculateNextStepPosition(currentPosition, direction) {
		if(!$.isArray(currentPosition)) {
			throw "not an array";
		}
		
		var xpos, ypos;
		
		switch(direction) {
			case 'right': 
				xpos = (currentPosition[0] + config.stepWidth + config.marginX);
				ypos = currentPosition[1] + decline; 
				break;
			case 'left': 
				xpos = (currentPosition[0] - config.stepWidth - config.marginX);
				ypos = currentPosition[1] + decline;
				break;
			case 'under': 
				xpos = currentPosition[0];
				ypos = (currentPosition[1] + config.stepHeight + config.marginY);
				break;
		}
		return [xpos, ypos];
	}
	
	function createStep(stepData) {
		
		var step = $('<div data-toggle="popover" title="Step #' + stepData.id + ' price $1" data-content="And here some amazing content. It is very engaging. Right?" />', {
			
		}).width(config.stepWidth)
			.height(config.stepHeight)
			.attr("id",'step-' + stepData.id)
			.addClass("step")
			.click(function(e) {
				e.preventDefault();
				console.log(this.id)
			}).css({
		        top: stepData.y  + "px",
		        left: stepData.x + "px"
		    });
		
		// add popover for steps
		$(step).popover({
			trigger : 'hover',
			placement : 'auto top'
		});
		
		return step;
	}
	

	function addStepsSection() {
		var direction = "right";
		var done = false;
		var lastStep = stepsCollection.last();
		var nextPosition = [lastStep.x, lastStep.y];
		
		while(!done) {
			if(direction === "right") {
				if(nextPosition[0] + config.stepWidth < containerWidth) {
					nextPosition = calculateNextStepPosition(nextPosition, 'right')
					stepsCollection.add(nextPosition[0], nextPosition[1]);
				} else {
					nextPosition = calculateNextStepPosition(nextPosition, 'under')
					stepsCollection.add(nextPosition[0], nextPosition[1]);
					
					nextPosition = calculateNextStepPosition(nextPosition, 'under')
					stepsCollection.add(nextPosition[0], nextPosition[1]);
					
					nextPosition = calculateNextStepPosition(nextPosition, 'under')
					stepsCollection.add(nextPosition[0], nextPosition[1]);
					
					direction = "left";
				}
			}
		
			if(direction === "left") {
				if(nextPosition[0] - config.stepWidth > 0) {
					nextPosition = calculateNextStepPosition(nextPosition, 'left')
					stepsCollection.add(nextPosition[0], nextPosition[1]);
				} else {
					nextPosition = calculateNextStepPosition(nextPosition, 'under')
					stepsCollection.add(nextPosition[0], nextPosition[1]);
					
					nextPosition = calculateNextStepPosition(nextPosition, 'under')
					stepsCollection.add(nextPosition[0], nextPosition[1]);
					
					nextPosition = calculateNextStepPosition(nextPosition, 'under')
					stepsCollection.add(nextPosition[0], nextPosition[1]);
					
					done = true;
				}
			}
		}
	}
	
	
	function drawSteps(from, to) {
		for(var i=from; i<to; i++) {
			config.container.append(createStep(stepsCollection.array[i]));
		}
		config.container.height(stepsCollection.last().y + 50);
	}
	
	
	// ~ Draw dolly functions =========================================================================================
	
	function drawDolly(step) {
		var dolly = $('<div/>', {
			"id" : 'dolly',
			"class" : config.dolly.cssClass,
			"click" : function(e) {
				e.preventDefault();
				jumpToNextStep()
			}
		});
		
		var pos = $(step).position();
		
		$(dolly).css({
	        top: pos.top + config.dolly.offset[1],
	        left: step.position().left + config.dolly.offset[0]
	    });
		
		config.container.append(dolly);
	}
	
	function jumpToNextStep() {
		console.log("Jumping")
		
		var currentStep = $("#step-" + config.dolly.step).position();
		config.dolly.step++;
		var nextStep = $("#step-" + config.dolly.step).position();
		
		var moveY = ("+=" + (nextStep.top - currentStep.top));
		var moveX = ("-=" + (currentStep.left - nextStep.left))
		
		$("#dolly").animate({ "left" : moveX, "top" : moveY}, "slow" );
		
		if(nextStep.left >= currentStep.left) {
			$("#dolly").removeClass("dollyFlip")
		} else { // moving left
			$("#dolly").addClass("dollyFlip")
		}
	}
	
	// ~ APIs =========================================================================================================
	
	function initiateRoad() {
		for (var i = 0; i < config.sections; i++) {
			addStepsSection();
		}
		
		drawSteps(0, stepsCollection.last().id);
			
		setTimeout(function() {
			var step = $("#step-" + config.dolly.step);
		    drawDolly(step);	
		}, 1000);	 
	}
	
	initiateRoad();
	
	
	// show hide scroll to top arrow
	$(window).scroll(function() {
		
		// infinite scroll
		if ($(window).scrollTop() == $(document).height() - $(window).height()) {
			console.log("adding road");
			var last = stepsCollection.last();
			
			addStepsSection();
			
			drawSteps(last.id, stepsCollection.last().id);
			
		}
		
	});
	
})(jQuery); // End of use strict
