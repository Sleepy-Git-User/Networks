pesq_score = pesq('input.wav', 'output.wav');
if pesq_score > 3.5
    disp("Good Quality : "+ pesq_score)
else
    disp("Bad Quality : "+ pesq_score)
end

