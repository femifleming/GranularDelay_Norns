-- Granular Delay
-- sound design by @femifleming
-- audio input required

engine.name = 'GranularDelay'

  function init()
	engine.load("GranularDelay")
end


function redraw()
  screen.clear()
  screen.move(0,30)
  screen.level(15)
  screen.text("Granular Delay")
  screen.update()
end