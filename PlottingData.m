dataPoints = 60;

[senderData] = ReadDataFromFile(".\measurements\sender.txt", dataPoints, 3);
[receiverData] = ReadDataFromFile(".\measurements\receiver.txt", dataPoints, 3);

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
line3 = zeros(1,15);
line4 = zeros(1,15);

for i = 1:dataPoints
    timediff = receiverTime(i) - senderTime(i);
    q = floor(i / 15);
    idx = mod(i - 1, 15) + 1; % Use the modulus operator to cycle through the indices of the arrays
    if q < 1
        line1(idx) = timediff;
    elseif q < 2
        line2(idx) = timediff;
    elseif q < 3
        line3(idx) = timediff;
    else
        line4(idx) = timediff;
    end
end

subplot(2, 1, 1) %row, column, position
plot(xAxis, line1, xAxis, line2, xAxis, line3, xAxis, line4) % Plot all four lines in one call to plot
xlim([0 15]);
ylim([0 10000]);
grid on
title('Graph to show the difference in delay between using different sizes of interleaving')
xlabel('Block')
ylabel('Delay')
legend('4 packet', '9 packet', '16 packet', '25 packet')
yticks(0:250:5000) % Set the y-axis tick marks every 250 ms
yticklabels([compose('%d ms', (0:250:1000)), strcat(num2str((1:10)'), ' s')]) % Set the y-axis tick labels in ms and s
ylabel('Time') % Add y-axis label






