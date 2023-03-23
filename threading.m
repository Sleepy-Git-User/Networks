dataPoints = 30;

[senderData] = ReadDataFromFile(".\measurements\Sunthread.txt", dataPoints, 3);
[receiverData] = ReadDataFromFile(".\measurements\Runthread.txt", dataPoints, 3);

xAxis = 0:14;

senderI = senderData(:,1);
senderHeader = senderData(:,2);
senderTime = senderData(:,3);

receiverI = receiverData(:,1);
receiverHeader = receiverData(:,2);
receiverTime = receiverData(:,3);

timediff = zeros(1, dataPoints);
line1 = zeros(1,15);
line2 = zeros(1,15);

for i = 1:dataPoints
    timediff = receiverTime(i) - senderTime(i);
    if receiverI(i) == 0
        line1(i) = timediff;
    else
        line2(i-15) = timediff;
    end
end

disp(line2);



subplot(2, 1, 1) %row, column, position
plot(xAxis, line1, xAxis, line2) % Plot all four lines in one call to plot
xlim([0 15]);
ylim([0 500]);
grid on
title('Graph to show the difference in delay between using different sizes of interleaving')
xlabel('Block')
ylabel('Delay')
legend('Double Thread', 'Tripple Thread')
